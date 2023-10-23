package core;

import core.service.OrderService;
import db.LoggedUsersRepository;
import models.Message;
import models.User;
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

    @Test
    void handleHelp() {
        Message msg = new Message();
        msg.setText("/help");
        Assertions.assertEquals(commandHandler.handle(msg), "help");
    }

    @Test
    void handleStart() {
        Message msg = new Message();
        msg.setText("/start");
        Assertions.assertEquals(commandHandler.start(msg), "Привет. Напиши /help0");
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
        Assertions.assertEquals("напишите /start", handel);
    }

    @Test
    void handleCreateOrder() throws SQLException {
        Message msg = new Message();
        msg.setText("/create_order");
        User user = new User(1, "1", "1");
        Mockito.when(
                loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                        msg.getPlatform(),
                        msg.getUserIdOnPlatform())
        ).thenReturn(user);
        Mockito.when(
                orderService.startCreateOrder(user.getId())
        ).thenReturn("Отработал startCreateOrder");
        String handel = commandHandler.handle(msg);
        Assertions.assertEquals("Отработал startCreateOrder", handel);
    }

    @Test
    void handleUpdateOrder() throws SQLException {
        Message msg = new Message();
        msg.setText("/update_order");
        User user = new User(1, "1", "1");
        Mockito.when(
                loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                        msg.getPlatform(),
                        msg.getUserIdOnPlatform())
        ).thenReturn(user);
        Mockito.when(
                orderService.startCreateOrder(user.getId())
        ).thenReturn("Отработал startUpdateOrder");
        String handel = commandHandler.handle(msg);
        Assertions.assertEquals("Отработал startUpdateOrder", handel);
    }

    @Test
    void handleCancelOrder() throws SQLException {
        Message msg = new Message();
        msg.setText("/cancel_order");
        User user = new User(1, "1", "1");
        Mockito.when(
                loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                        msg.getPlatform(),
                        msg.getUserIdOnPlatform())
        ).thenReturn(user);
        Mockito.when(
                orderService.startCreateOrder(user.getId())
        ).thenReturn("Отработал startCancelOrder");
        String handel = commandHandler.handle(msg);
        Assertions.assertEquals("Отработал startCancelOrder", handel);
    }

    @Test
    void handleViewListOrder() throws SQLException {
        Message msg = new Message();
        msg.setText("/view_list_order");
        User user = new User(1, "1", "1");
        Mockito.when(
                loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                        msg.getPlatform(),
                        msg.getUserIdOnPlatform())
        ).thenReturn(user);
        Mockito.when(
                orderService.startCreateOrder(user.getId())
        ).thenReturn("Отработал viewLisrOrder");
        String handel = commandHandler.handle(msg);
        Assertions.assertEquals("Отработал viewLisrOrder", handel);
    }

    @Test
    void handleRandText() throws SQLException {
        Message msg = new Message();
        msg.setText("rand");
        User user = new User(1, "1", "1");
        Mockito.when(
                loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                        msg.getPlatform(),
                        msg.getUserIdOnPlatform())
        ).thenReturn(user);
        String handel = commandHandler.handle(msg);
        Assertions.assertEquals("Извините я вас не понимаю. Напишите /help.", handel);
    }

}