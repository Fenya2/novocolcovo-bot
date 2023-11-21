package core.service_handlers.services;

import core.UserNotifier;
import db.DBException;
import db.LoggedUsersRepository;
import db.LoggingUsersRepository;
import db.UserRepository;
import models.Domain;
import models.Platform;
import models.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class LoginSeviceTest {
    @InjectMocks
    private LoginService loginService;
    @Mock
    private LoggingUsersRepository loggingUsersRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LoggedUsersRepository loggedUsersRepository;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }


    /**
     * Проверяет, что в случает успешного начала авторизации отправляется нужное сообщение
     * пользователю.
     */
    @Test
    public void testStartSession() throws DBException {
        String userIdOnPlatform = "someUserIdOnPlatform";
        Platform platform = Platform.TELEGRAM;
        Assert.assertEquals(
                "Введите ваш логин пользователя",
                loginService.startSession(platform, userIdOnPlatform)
        );
    }

    /**
     * Проверяет, что отправляется пользователю после отправки логина, который есть
     * в бд. Проверяет, что записывается в domain.
     */
    @Test
    public void testContinueSessionWhenLoginCorrect() throws DBException, SQLException {
        Domain domain = new Domain()
                .fromPlatform(Platform.TELEGRAM)
                .requiredLogin("no login yet")
                .loginContext(0);
        User user = new User(10, "name", "description", "login");
        List<Platform> platforms = Arrays.asList(Platform.VK, Platform.TELEGRAM);
        Mockito.when(userRepository.getByLogin("login")).thenReturn(user);
        Mockito.when(loggedUsersRepository.getPlatformsByUserId(10))
                        .thenReturn(platforms);
        Assert.assertEquals(
                "Введите платформу, куда будет отправлен код подтверждения.\n/VK\n/TELEGRAM",
                loginService.continueSession(
                        Platform.TELEGRAM,
                        "userIdOnPlatform",
                        "login",
                        domain
                        ));
        Assert.assertEquals("login", domain.getRequiredLogin());
        Assert.assertEquals(1, domain.getLoginContext());
    }

    /**
     * Проверяет, что отправляется пользователю после отправки логина, которого нет в базе.
     */
    @Test
    public void testContinueSessionWhenLoginIncorrect() throws DBException, SQLException {
        Domain domain = new Domain()
                .fromPlatform(Platform.TELEGRAM)
                .requiredLogin("no login yet")
                .loginContext(0);
        User user = new User(10, "name", "description", "login");
        Mockito.when(userRepository.getByLogin("login")).thenReturn(null);
        Assert.assertEquals(
                "Такого логина не существует. Попробуйте еще раз.",
                loginService.continueSession(
                        Platform.TELEGRAM,
                        "userIdOnPlatform",
                        "login",
                        domain
                ));
    }

    /**
     * Проверяет, что отправляется пользователю после отправки платформы корректной
     * платформы, на которую можно отправить код подтверждения.
     */
    @Test
    public void testContinueSessionWhenPLatformCorrect() throws DBException, SQLException {
        Domain domain = new Domain()
                .requiredLogin("login")
                .fromPlatform(Platform.NO_PLATFORM)
                .loginContext(1);
        User user = new User(10, "name", "description", "login");
        Mockito.when(userRepository.getByLogin("login")).thenReturn(user);
        loginService.setUserNotifier(Mockito.mock(UserNotifier.class));
        Assert.assertEquals(
                "Мы отправили 4-хзначный код на указанную платформу. Введите его, чтобы войти",
                loginService.continueSession(
                        Platform.TELEGRAM,
                        "userIdOnPlatform",
                        "/TELEGRAM",
                        domain
                ));
        Assert.assertEquals(Platform.TELEGRAM, domain.getVerificationPlatform());
        Assert.assertEquals(2, domain.getLoginContext());
    }

    /**
     * Проверяет, что отправляется пользователю после отправки некорректной платформы,
     * на которую нельзя отправить сообщение с кодом подтверждения
     */
    @Test
    public void testContinueSessionWhenPlatformInvalid() throws DBException, SQLException {
        Domain domain = new Domain()
                .requiredLogin("login")
                .fromPlatform(Platform.NO_PLATFORM)
                .loginContext(1);
        Assert.assertEquals(
                "Платформа указана некорректно. Попробуйте еще раз.",
                loginService.continueSession(
                        Platform.TELEGRAM,
                        "userIdOnPlatform",
                        "incorrect platform",
                        domain
                ));
    }

    /**
     * Проверяет, что отправляется пользователю после успешной авторизации по коду.
     */
    @Test
    public void testContinueSessionWhenCodeIsValid() throws DBException, SQLException {
        Domain domain = new Domain()
                .requiredLogin("login")
                .fromPlatform(Platform.TELEGRAM)
                .loginContext(2)
                .verificationCode(1000);
        User user = new User(10, "name", "description", "login");
        Mockito.when(userRepository.getByLogin("login")).thenReturn(user);
        loginService.setUserNotifier(Mockito.mock(UserNotifier.class));
        Assert.assertEquals(
                "Вы успешно авторизовались.",
                loginService.continueSession(
                        Platform.TELEGRAM,
                        "userIdOnPlatform",
                        "1000",
                        domain
                ));
    }

    /**
     * Проверяет, что отправляется пользователю после отправки некорректной платформы,
     * на которую нельзя отправить сообщение с кодом подтверждения
     */
    @Test
    public void testContinueSessionWhenCodeIsInvalid() throws DBException, SQLException {
        Domain domain = new Domain()
                .requiredLogin("login")
                .fromPlatform(Platform.TELEGRAM)
                .loginContext(2)
                .verificationCode(1000);
        User user = new User(10, "name", "description", "login");
        Mockito.when(userRepository.getByLogin("login")).thenReturn(user);
        loginService.setUserNotifier(Mockito.mock(UserNotifier.class));
        Assert.assertEquals(
                "Неверный код. Попробуйте еще раз",
                loginService.continueSession(
                        Platform.TELEGRAM,
                        "userIdOnPlatform",
                        "1178",
                        domain
                ));
        Assert.assertEquals(
                "Неверный код. Попробуйте еще раз",
                loginService.continueSession(
                        Platform.TELEGRAM,
                        "userIdOnPlatform",
                        "blabla",
                        domain
                ));

        Assert.assertEquals(
                "Неверный код. Попробуйте еще раз",
                loginService.continueSession(
                        Platform.TELEGRAM,
                        "userIdOnPlatform",
                        "19283091273",
                        domain
                ));
    }
}
