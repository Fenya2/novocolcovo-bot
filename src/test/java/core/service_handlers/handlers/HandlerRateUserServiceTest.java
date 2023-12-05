package core.service_handlers.handlers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import bots.Bot;
import core.service_handlers.services.RateUserService;
import models.Message;
import models.User;
import models.UserContext;
import models.UserState;

/**
 * Тест на класс - обработчик сообщений сервиса RateUserService.
 */
public class HandlerRateUserServiceTest {
    @InjectMocks
    HandlerRateUserService handlerRateUserService;
    @Mock
    RateUserService rateUserService;
    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Проверяет логику метода handle, когда передается корректная команда в контексте
     * EditUserService.
     */
    @Test
    public void testHandleWhenCommandValid() {
        Message message = new Message();
        message.setUser(new User(10, "name", "description", "login"));
        message.setBotFrom(Mockito.mock(Bot.class));
        message.setText("/help");
        Assert.assertEquals(1, handlerRateUserService.handle(message));
    }

    /**
     * Проверяет работу handle, когда введен корректный рейтинг
     */
    @Test
    public void testHandleWhenMessageTextLinkedToContext() {
        Message message = new Message();
        message.setUser(new User(10, "name", "description", "login"));
        message.setUserContext(new UserContext(UserState.EDIT_USER, 1));
        message.setBotFrom(Mockito.mock(Bot.class));
        message.setText("4");
        Assert.assertEquals(2, handlerRateUserService.handle(message));
    }

    /**
     * Проверяет логику метода handle,
     * когда отправленное сообщение некорректно.
     */
    @Test
    public void testHandleWhenMessageTextHaveNoContext() {
        Message message = new Message();
        message.setUser(new User(10, "name", "description", "login"));
        message.setUserContext(new UserContext(UserState.EDIT_USER, 0));
        message.setBotFrom(Mockito.mock(Bot.class));
        message.setText("some text not num.");
        Assert.assertEquals(-1, handlerRateUserService.handle(message));
    }
}
