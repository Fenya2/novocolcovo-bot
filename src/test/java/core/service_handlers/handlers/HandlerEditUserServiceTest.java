package core.service_handlers.handlers;

import bots.Bot;
import core.service_handlers.services.EditUserService;
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

public class HandlerEditUserServiceTest {
    @InjectMocks
    HandlerEditUserService handlerEditUserService;
    @Mock
    EditUserService editUserService;
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
        message.setUser(new User(10, "name", "description"));
        message.setBotFrom(Mockito.mock(Bot.class));
        message.setText("/show_profile");
        Assert.assertEquals(1, handlerEditUserService.handle(message));
    }

    /**
     * Проверяет логику метода handle, когда передается сообщение, привязанное к контексту внутри
     * EditUserService (edit_username или edit_description)
     */
    @Test
    public void testHandleWhenMessageTextLinkedToContext() {
        Message message = new Message();
        message.setUser(new User(10, "name", "description"));
        message.setUserContext(new UserContext(UserState.EDIT_USER, 1));
        message.setBotFrom(Mockito.mock(Bot.class));
        message.setText("new_username");
        Assert.assertEquals(2, handlerEditUserService.handle(message));

        message.setUserContext(new UserContext(UserState.EDIT_USER, 2));
        Assert.assertEquals(2, handlerEditUserService.handle(message));
    }

    /**
     * Проверяет логику метода handle, когда передается сообщение, которое не привяно к контексту.
     */
    @Test
    public void testHandleWhenMessageTextHaveNoContext() {
        Message message = new Message();
        message.setUser(new User(10, "name", "description"));
        message.setUserContext(new UserContext(UserState.EDIT_USER, 0));
        message.setBotFrom(Mockito.mock(Bot.class));
        message.setText("do something stupid.");
        Assert.assertEquals(3, handlerEditUserService.handle(message));
    }

    /**
     * Проверяет логику метода handle, когда передается сообщение,
     * которое является некорректной командой.
     */
    @Test
    public void testHandleWhenMessageIsNotValidCommand() {
        Message message = new Message();
        message.setUser(new User(10, "name", "description"));
        message.setUserContext(new UserContext(UserState.EDIT_USER, 0));
        message.setBotFrom(Mockito.mock(Bot.class));
        message.setText("/some_not_valid_command");
        Assert.assertEquals(3, handlerEditUserService.handle(message));
    }
}
