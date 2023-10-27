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

    /**
     * Проыеряет случай когда пользователь не найден(=null)
     */
    @Test
    void handleUserNull() throws SQLException {
        Message msg = new Message();
        Mockito.when(
                loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                        Mockito.any(),
                        Mockito.any())
        ).thenReturn(null);
        String handel = textHandler.handle(msg);
        Assertions.assertEquals("напишите /start", handel);
    }

    /**
     * Проверяет случай когда у найденного пользователя нету контекста
     */
    @Test
    void handleUserContextNull() throws SQLException {
        Message msg = new Message();
        User user = new User();
        Mockito.when(
                loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                        Mockito.any(),
                        Mockito.any())
        ).thenReturn(user);
        Mockito.when(
                userContextRepository.getUserContext(user.getId())
        ).thenReturn(null);
        String handel = textHandler.handle(msg);
        Assertions.assertEquals("я вас не понимаю", handel);
    }

    /**
     * Проверяет случай когда контекст пользователя не null и равен
     * "create_order","edit_order","cancel_order"
     */
    @Test
    void handleCreateEditCancelOrder() throws SQLException {
        Message msg = new Message();
        User user = new User();
        UserContext userContext = new UserContext("create_order");
        Mockito.when(
                loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                        Mockito.any(),
                        Mockito.any())
        ).thenReturn(user);

        Mockito.when(
                userContextRepository.getUserContext(user.getId())
        ).thenReturn(userContext);

        Mockito.when(
                orderService.continueCreateOrder(user.getId(),msg.getText())
        ).thenReturn("Отработал continueCreateOrder");
        String handel1 = textHandler.handle(msg);
        Assertions.assertEquals("Отработал continueCreateOrder", handel1);

        userContext.setState("edit_order");
        Mockito.when(
                orderService.continueEditOrder(user.getId(),msg.getText())
        ).thenReturn("Отработал continueEditOrder");
        String handel2 = textHandler.handle(msg);
        Assertions.assertEquals("Отработал continueEditOrder", handel2);

        userContext.setState("cancel_order");
        Mockito.when(
                orderService.continueCancelOrder(user.getId(),msg.getText())
        ).thenReturn("Отработал continueCancelOrder");
        String handel3 = textHandler.handle(msg);
        Assertions.assertEquals("Отработал continueCancelOrder", handel3);
    }
}