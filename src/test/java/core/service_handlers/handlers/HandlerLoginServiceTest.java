package core.service_handlers.handlers;

import bots.Bot;
import core.service_handlers.services.LoginService;
import models.Domain;
import models.Message;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class HandlerLoginServiceTest {
    @InjectMocks
    private HandlerLoginService handlerLoginService;
    @Mock
    private LoginService loginService;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Проверяет работу, когда приходит корректная команда.
     */
    @Test
    public void testHandleWhenTextMessageIsValidCommand() {
        Message message = new Message();
        message.setText("/help");
        message.setUserIdOnPlatform("userIdOnPlatform");
        message.setBotFrom(Mockito.mock(Bot.class));
        Domain domain = Mockito.mock(Domain.class);
        Assert.assertEquals(1, handlerLoginService.handle(message,domain));
        message.setText("/cancel");
        Assert.assertEquals(1, handlerLoginService.handle(message,domain));
    }

    /**
     * Проверяет работу, когда приходит сообщение,
     * привязанное к контексту авторизации пользователя,
     * которое будет отправлено на обработку сервису.
     */
    @Test
    public void testHandleWhenTextMessageBelongsToAuthContext() {
        Message message = new Message();
        message.setText("some text, not command.");
        message.setUserIdOnPlatform("userIdOnPlatform");
        message.setBotFrom(Mockito.mock(Bot.class));
        Domain domain = Mockito.mock(Domain.class);
        Assert.assertEquals(2, handlerLoginService.handle(message,domain));
    }
}
