package core;

import core.service.OrderService;
import db.LoggedUsersRepository;
import db.UserContextRepository;
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


class TextHandlerTest {
    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @InjectMocks
    private TextHandler textHandler;
    @Mock
    private OrderService orderService;
    @Mock
    private LoggedUsersRepository loggedUsersRepository;
    @Mock
    private UserContextRepository userContextRepository;

    @Test
    void handleUserNull() throws SQLException {
        Message msg = new Message();
        Mockito.when(
                loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                        msg.getPlatform(),
                        msg.getUserIdOnPlatform())
        ).thenReturn(null);
        String handel = textHandler.handle(msg);
        Assertions.assertEquals("напишите /start", handel);
    }

    @Test
    void handleUserContextNull() throws SQLException {
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
        String handel = textHandler.handle(msg);
        Assertions.assertEquals("я вас не понимаю", handel);
    }

    @Test
    void handleCreateUpdateCancelOrder() throws SQLException {
        Message msg = new Message();
        User user = new User();
        UserContext userContext = new UserContext("create_order");
        Mockito.when(
                loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                        msg.getPlatform(),
                        msg.getUserIdOnPlatform())
        ).thenReturn(user);

        Mockito.when(
                userContextRepository.getUserContext(user.getId())
        ).thenReturn(userContext);

        Mockito.when(
                orderService.continueCreateOrder(user.getId(),msg.getText())
        ).thenReturn("Отработал continueCreateOrder");
        String handel1 = textHandler.handle(msg);
        Assertions.assertEquals("Отработал continueCreateOrder", handel1);

        userContext.setState("update_order");
        Mockito.when(
                orderService.continueUpdateOrder(user.getId(),msg.getText())
        ).thenReturn("Отработал continueUpdateOrder");
        String handel2 = textHandler.handle(msg);
        Assertions.assertEquals("Отработал continueUpdateOrder", handel2);

        userContext.setState("cancel_order");
        Mockito.when(
                orderService.continueСancelOrder(user.getId(),msg.getText())
        ).thenReturn("Отработал continueCancelOrder");
        String handel3 = textHandler.handle(msg);
        Assertions.assertEquals("Отработал continueCancelOrder", handel3);

        userContext.setState("change_username");
        Mockito.when(
                userContextRepository.getUserContext(user.getId())
        ).thenReturn(userContext);
        String handel4 = textHandler.handle(msg);
        Assertions.assertEquals("Извините, я вас не понял. Вызовите команду /help", handel4);

    }
}