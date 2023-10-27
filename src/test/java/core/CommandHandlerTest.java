package core;

import core.service.OrderService;
import db.LoggedUsersRepository;
import db.UserContextRepository;
import db.UserRepository;
import models.Message;
import models.User;
import models.UserContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;

class CommandHandlerTest {
    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @InjectMocks
    private CommandHandler commandHandler;
    @Mock
    private OrderService orderService;
    @Mock
    private LoggedUsersRepository loggedUsersRepository;
    @Mock
    private UserContextRepository userContextRepository;
    @Mock
    private UserRepository userRepository;

    /**
     * Проверяет случай когда пользователь вводит команду /help
     */
    @Test
    void handleHelp() {
        Message msg = new Message();
        msg.setText("/help");
        Assertions.assertEquals(commandHandler.handle(msg), """
                /create_order - создать заказ
                /edit_order - изменить заказ
                /cancel_order - удалить заказ
                /show_order - посмотреть список заказов
                """);
    }

    /**
     * Проверяет случай когда пользователь вводит команду /start
     */
    @Test
    void handleStart() throws SQLException {
        Message msg = new Message();
        msg.setText("/start");
        Mockito.when(loggedUsersRepository.getUserByPlatformAndIdOnPlatform(Mockito.any(),Mockito.any()))
                .thenReturn(null);
        Mockito.when(userRepository.save(Mockito.any()))
                        .thenReturn(new User());
        Assertions.assertEquals(commandHandler.start(msg), "Привет. Напишите /help");

        Mockito.when(loggedUsersRepository.getUserByPlatformAndIdOnPlatform(Mockito.any(),Mockito.any()))
                .thenReturn(new User());
        Assertions.assertEquals(commandHandler.start(msg), "Привет. Напишите /help");
    }

    /**
     * Проверяет случай когда команда не /start и не /help<br>
     * и пользователь не найден
     */
    @Test
    void handleUserNull() throws SQLException {
        Message msg = new Message();
        Mockito.when(
                loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                        Mockito.any(),Mockito.any())
        ).thenReturn(null);
        String handel = commandHandler.handle(msg);
        Assertions.assertEquals("Напишите /start", handel);
    }

    /**
     * Проверяет случай когда команда не /start и не /help <br>
     * и у найденного пользователя есть контекст
     */
    @Test
    void handleUserContextNotNull() throws SQLException {
        Message msg = new Message();
        User user = new User();
        Mockito.when(
                loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                        Mockito.any(),
                        Mockito.any())
        ).thenReturn(user);
        Mockito.when(
                userContextRepository.getUserContext(user.getId())
        ).thenReturn(new UserContext("create_order", 0));
        String handel = commandHandler.handle(msg);
        Assertions.assertEquals("Сейчас команды не доступны", handel);
    }

    /**
     * Проверяет случай когда команда не /start и не /help <br>
     * у найденного пользователя нету контекста<br>
     */
    @Test
    void handleCreateEditCancelShowOrder() throws SQLException {
        Message msg = new Message();
        User user = new User();
        Mockito.when(
                loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                        msg.getPlatform(),
                        msg.getUserIdOnPlatform())
        ).thenReturn(user);
        Mockito.when(
                userContextRepository.getUserContext(user.getId())
        ).thenReturn(null);

        msg.setText("/create_order");
        Mockito.when(
                orderService.startCreateOrder(user.getId())
        ).thenReturn("Отработал startCreateOrder");
        String handel1 = commandHandler.handle(msg);
        Assertions.assertEquals("Отработал startCreateOrder", handel1);

        msg.setText("/edit_order");
        Mockito.when(
                orderService.startEditOrder(user.getId())
        ).thenReturn("Отработал startEditOrder");
        String handel2 = commandHandler.handle(msg);
        Assertions.assertEquals("Отработал startEditOrder", handel2);

        msg.setText("/cancel_order");
        Mockito.when(
                orderService.startCancelOrder(user.getId())
        ).thenReturn("Отработал startCancelOrder");
        String handel3 = commandHandler.handle(msg);
        Assertions.assertEquals("Отработал startCancelOrder", handel3);

        msg.setText("/show_order");
        Mockito.when(
                orderService.showOrder(user.getId())
        ).thenReturn("Отработал showOrder");
        String handel4 = commandHandler.handle(msg);
        Assertions.assertEquals("Отработал showOrder", handel4);

        msg.setText("/rand");
        String handel = commandHandler.handle(msg);
        Assertions.assertEquals("Извините я вас не понимаю. Напишите /help.", handel);

    }
}