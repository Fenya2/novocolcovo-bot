package core;

import core.service.OrderService;
import db.LoggedUsersRepository;
import db.UserContextRepository;
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


public class TextHandlerTest {
    @Before
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

    /** Проыеряет случай когда пользователь не найден(=null). */
    @Test
    public void handleUserNull() throws SQLException {
        Message msg = new Message();
        Mockito.when(
                loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                        Mockito.any(),
                        Mockito.any())
        ).thenReturn(null);
        String handle = textHandler.handle(msg);
        Assert.assertEquals("напишите /start", handle);
    }

    /** Проверяет случай когда у найденного пользователя нету контекста. */
    @Test
    public void handleUserContextNull() throws SQLException {
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
        String handle = textHandler.handle(msg);
        Assert.assertEquals("я вас не понимаю", handle);
    }

    /**
     * Проверяет случай когда контекст пользователя не null и равен
     * "create_order","edit_order","cancel_order".
     */
    @Test
    public void handleCreateEditCancelOrder() throws SQLException {
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
        String handle1 = textHandler.handle(msg);
        Assert.assertEquals("Отработал continueCreateOrder", handle1);

        userContext.setState("edit_order");
        Mockito.when(
                orderService.continueEditOrder(user.getId(),msg.getText())
        ).thenReturn("Отработал continueEditOrder");
        String handle2 = textHandler.handle(msg);
        Assert.assertEquals("Отработал continueEditOrder", handle2);

        userContext.setState("cancel_order");
        Mockito.when(
                orderService.continueCancelOrder(user.getId(),msg.getText())
        ).thenReturn("Отработал continueCancelOrder");
        String handle3 = textHandler.handle(msg);
        Assert.assertEquals("Отработал continueCancelOrder", handle3);
    }
}