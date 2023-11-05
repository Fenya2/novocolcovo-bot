package core.service_handlers.handlers;

import core.CommandHandler;
import core.ServiceManager;
import models.Message;
import models.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import strubs.BotStrub;

public class CommandHandlerTest {
    @InjectMocks
    CommandHandler commandHandler;
    @Mock
    private ServiceManager serviceManager;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    /** Проверяет работу handle, когда в переденном сообщении не команда. */
    @Test
    public void testHandleWhenNoCommandMessage() {
        Message message = new Message();
        message.setText("text. Not command");
        message.setBotFrom(new BotStrub());
        Assert.assertEquals(1, commandHandler.handle(message));
    }

    /** Проверяет работу handle, когда в переденном сообщении - корректная команда. */
    @Test
    public void testHandleWhenCommandIsValid() {
        Message message = new Message();
        message.setUser(new User(10, "username", "description"));
        message.setText("/edit_user");
        message.setBotFrom(new BotStrub());
        Assert.assertEquals(2, commandHandler.handle(message));
        message.setText("/create_order");
        Assert.assertEquals(2, commandHandler.handle(message));
    }

    /** Проверяет работу handle, когда в переденном сообщении - некорректная команда. */
    @Test
    public void testHandleWhenCommandIsNotValid() {
        Message message = new Message();
        message.setUser(new User(10, "username", "description"));
        message.setText("/calculate_determinant");
        message.setBotFrom(new BotStrub());
        Assert.assertEquals(3, commandHandler.handle(message));
    }
}
