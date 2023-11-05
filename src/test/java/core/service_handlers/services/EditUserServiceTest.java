package core.service_handlers.services;

import core.service_handlers.services.EditUserService;
import db.UserContextRepository;
import db.UserRepository;
import models.User;
import models.UserContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;

public class EditUserServiceTest {
    @InjectMocks
    private EditUserService editUserService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserContextRepository userContextRepository;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    /** Считаем, что сохранение в базу данных работает идеально. */
    @BeforeEach
    public void beforeEach() throws SQLException {
        MockitoAnnotations.openMocks(this);
        Mockito.when(userRepository.updateUser(Mockito.any()))
                .thenReturn(1);
        Mockito.when(userContextRepository.updateUserContext(Mockito.anyLong(), Mockito.any()))
                .thenReturn(1);
    }

    /** Принимает новое имя и пользователя, возвращает пользователя с новым именем. */
    @Test
    public void testUpdateUsername() throws SQLException {
        User user = new User(1, "name", "description");
        editUserService.updateUsername("new_name", user);
        Assert.assertEquals("new_name" , user.getName());
    }

    /**
     * Принимает новое описание пользователя и пользователя,
     * возвращает пользователя с новым описанием.
     */
    @Test
    public void testUpdateDescription() throws SQLException {
        User user = new User(1, "name", "description");
        editUserService.updateDescription("new_description", user);
        Assert.assertEquals("new_description", user.getDescription());
    }

    /**
     * проверяет, что устанавливается нужный контекст для пользователя,
     * который захочет изменить имя.
     */
    @Test
    public void testSetEditUsernameContext() throws SQLException {
        UserContext userContext =  editUserService.setEditUsernameContext(10);
        Assert.assertEquals("EDIT_USER", userContext.getState().toString());
        Assert.assertEquals(1, userContext.getStateNum());
    }

    /**
     * проверяет, что устанавливается нужный контекст для пользователя,
     * который захочет изменить описание.
     */
    @Test
    public void testSetEditDescriptionContext() throws SQLException {
        UserContext userContext =  editUserService.setEditDescriptionContext(10);
        Assert.assertEquals("EDIT_USER", userContext.getState().toString());
        Assert.assertEquals(2, userContext.getStateNum());
    }

    /**
     * проверяет, что устанавливается нужный контекст для пользователя,
     * который изменил имя или описание и снова должен попасть в контекст редактирования
     * пользователя
     */
    @Test
    public void testResetEditContext() throws SQLException {
        UserContext userContext =  editUserService.resetEditContext(10);
        Assert.assertEquals("EDIT_USER", userContext.getState().toString());
        Assert.assertEquals(0, userContext.getStateNum());
    }

    /** Возвращает приветственное сообщение. */
    @Test
    public void testStartSession() throws SQLException {
        Assert.assertEquals("""
            Обновление вашего аккаунта.
            Напишите:
            /edit_username , чтобы изменить имя пользователя.
            /edit_description, чтобы изменить описание к вашему аккаунту.
            Напишите:
            /cancel для отмены действия.
            """, editUserService.startSession(10));
    }

    /** Возврващет сообщение с прощанием */
    @Test
    public void testEndSession() {
        Assert.assertEquals("Изменения успешно сохранены.",
                editUserService.endSession(10));
    }
}
