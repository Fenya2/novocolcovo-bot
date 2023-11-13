package core.service_handlers.handlers;

import bots.Bot;
import core.service_handlers.services.CancelOrderService;
import models.Message;
import models.User;
import models.UserContext;
import models.UserState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class HandlerCancelOrderServiceTest {
    @InjectMocks
    HandlerCancelOrderService handlerCancelOrderService;
    @Mock
    CancelOrderService cancelOrderService;
    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Проверяет логику метода handle, когда передается корректная команда в контексте
     * EditOrderService.
     */
    @Test
    public void testHandleWhenCommandValid() {
        Message message = new Message();
        message.setUser(new User(10, "name", "description", "login"));
        message.setBotFrom(Mockito.mock(Bot.class));
        message.setText("/cancel");
        Assert.assertEquals(1, handlerCancelOrderService.handle(message));
        message.setText("/help");
        Assert.assertEquals(1, handlerCancelOrderService.handle(message));
    }

    /**
     * Проверяет логику метода handle, когда передается некорректная команда в контексте
     * EditOrderService
     */
    @Test
    public void testHandleWhenMessageIsNotValidCommand() {
        Message message = new Message();
        message.setUser(new User(10, "name", "description", "login"));
        message.setUserContext(new UserContext(UserState.EDIT_USER, 0));
        message.setBotFrom(Mockito.mock(Bot.class));
        message.setText("/some_not_valid_command");
        Assert.assertEquals(3, handlerCancelOrderService.handle(message));
    }

    /**
     * Проверяет логику метода handle, когда передается текст сообщения - не команда, которая
     * далее передается в обработку сервису.
     */
    @Test
    public void testHandleWhenMessageIsNotCommand() {
        Message message = new Message();
        message.setUser(new User(10, "name", "description", "login"));
        message.setUserContext(new UserContext(UserState.EDIT_USER, 0));
        message.setBotFrom(Mockito.mock(Bot.class));
        message.setText("some_message_not_command");
        Assert.assertEquals(2, handlerCancelOrderService.handle(message));
        message.setText("72");
        Assert.assertEquals(2, handlerCancelOrderService.handle(message));
    }
}
