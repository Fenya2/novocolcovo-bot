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
    UserRepository userRepository;

    @Test
    void handleHelp() {
        Message msg = new Message();
        msg.setText("/help");
        Assertions.assertEquals(commandHandler.handle(msg), """
                /create_order - создать заказ
                /update_order - изменить заказ
                /cancel_order - удалить заказ
                /view_list_order - посмотреть список заказов
                """);
    }

    @Test
    void handleStart() throws SQLException {
        Message msg = new Message();
        msg.setText("/start");
        String platform = msg.getPlatform();
        String userIdOnPlatform = msg.getUserIdOnPlatform();
        Mockito.when(loggedUsersRepository.getUserByPlatformAndIdOnPlatform(platform, userIdOnPlatform))
                .thenReturn(new User());
        Assertions.assertEquals(commandHandler.start(msg), "Привет. Напишите /help");
    }

    @Test
    void handleUserNull() throws SQLException {
        Message msg = new Message();
        Mockito.when(
                loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                        msg.getPlatform(),
                        msg.getUserIdOnPlatform())
        ).thenReturn(null);
        String handel = commandHandler.handle(msg);
        Assertions.assertEquals("Напишите /start", handel);
    }

    @Test
    void handleUserContextNotNull() throws SQLException {
        Message msg = new Message();
        User user = new User();
        Mockito.when(
                loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                        msg.getPlatform(),
                        msg.getUserIdOnPlatform())
        ).thenReturn(user);
        Mockito.when(
                userContextRepository.getUserContext(user.getId())
        ).thenReturn(new UserContext("create_order", 0));
        String handel = commandHandler.handle(msg);
        Assertions.assertEquals("Сейчас команды не доступны", handel);
    }

    @Test
    void handleCreateUpdateCancelViewOrder() throws SQLException {
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

        msg.setText("/update_order");
        Mockito.when(
                orderService.startUpdateOrder(user.getId())
        ).thenReturn("Отработал startUpdateOrder");
        String handel2 = commandHandler.handle(msg);
        Assertions.assertEquals("Отработал startUpdateOrder", handel2);

        msg.setText("/cancel_order");
        Mockito.when(
                orderService.startСancelOrder(user.getId())
        ).thenReturn("Отработал startCancelOrder");
        String handel3 = commandHandler.handle(msg);
        Assertions.assertEquals("Отработал startCancelOrder", handel3);

        msg.setText("/view_list_order");
        Mockito.when(
                orderService.viewListOrder(user.getId())
        ).thenReturn("Отработал viewLisrOrder");
        String handel4 = commandHandler.handle(msg);
        Assertions.assertEquals("Отработал viewLisrOrder", handel4);

        msg.setText("rand");
        String handel = commandHandler.handle(msg);
        Assertions.assertEquals("Извините я вас не понимаю. Напишите /help.", handel);

    }
}