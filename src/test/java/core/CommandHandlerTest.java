package core;

import core.service.OrderService;
import db.LoggedUsersRepository;
import db.UserContextRepository;
import db.UserRepository;
import models.Message;
import models.User;
import models.UserContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;

public class CommandHandlerTest {
    @Before
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
     * Проверяет случай когда пользователь вводит команду /help.
     */
    @Test
    public void handleHelp() {
        Message msg = new Message();
        msg.setText("/help");
        Assert.assertEquals( """
                /create_order - создать заказ
                /edit_order - изменить заказ
                /cancel_order - удалить заказ
                /show_order - посмотреть список заказов
                """,commandHandler.handle(msg));
    }

    /**
     * Проверяет случай когда пользователь вводит команду /start.
     */
    @Test
    public void handleStart() throws SQLException {
        Message msg = new Message();
        msg.setText("/start");
        Mockito.when(loggedUsersRepository.getUserByPlatformAndIdOnPlatform(Mockito.any(),Mockito.any()))
                .thenReturn(null);
        Mockito.when(userRepository.save(Mockito.any()))
                        .thenReturn(new User());
        Assert.assertEquals("Привет. Напишите /help",commandHandler.start(msg));

        Mockito.when(loggedUsersRepository.getUserByPlatformAndIdOnPlatform(Mockito.any(),Mockito.any()))
                .thenReturn(new User());
        Assert.assertEquals("Привет. Напишите /help",commandHandler.start(msg));
    }

    /**
     * Проверяет случай когда команда не /start и не /help<br>
     * и пользователь не найден.
     */
    @Test
    public void handleUserNull() throws SQLException {
        Message msg = new Message();
        Mockito.when(
                loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                        Mockito.any(),Mockito.any())
        ).thenReturn(null);
        String handle = commandHandler.handle(msg);
        Assert.assertEquals("Напишите /start", handle);
    }

    /**
     * Проверяет случай когда команда не /start и не /help <br>
     * и у найденного пользователя есть контекст.
     */
    @Test
    public void handleUserContextNotNull() throws SQLException {
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
        String handle = commandHandler.handle(msg);
        Assert.assertEquals("Сейчас команды не доступны", handle);
    }

    /**
     * Проверяет случай когда команда не /start и не /help <br>
     * у найденного пользователя нету контекста<br>.
     */
    @Test
    public void handleCreateEditCancelShowOrder() throws SQLException {
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
        String handle1 = commandHandler.handle(msg);
        Assert.assertEquals("Отработал startCreateOrder", handle1);

        msg.setText("/edit_order");
        Mockito.when(
                orderService.startEditOrder(user.getId())
        ).thenReturn("Отработал startEditOrder");
        String handle2 = commandHandler.handle(msg);
        Assert.assertEquals("Отработал startEditOrder", handle2);

        msg.setText("/cancel_order");
        Mockito.when(
                orderService.startCancelOrder(user.getId())
        ).thenReturn("Отработал startCancelOrder");
        String handle3 = commandHandler.handle(msg);
        Assert.assertEquals("Отработал startCancelOrder", handle3);

        msg.setText("/show_order");
        Mockito.when(
                orderService.showOrder(user.getId())
        ).thenReturn("Отработал showOrder");
        String handle4 = commandHandler.handle(msg);
        Assert.assertEquals("Отработал showOrder", handle4);

        msg.setText("/rand");
        String handle = commandHandler.handle(msg);
        Assert.assertEquals("Извините я вас не понимаю. Напишите /help.", handle);

    }
}