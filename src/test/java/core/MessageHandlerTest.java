package core;

import models.Message;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class MessageHandlerTest {
    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }
    @InjectMocks
    private MessageHandler messageHandler;
    @Mock
    private TextHandler textHandler;
    @Mock
    private CommandHandler commandHandler;

    /** Проверяет правильность обработки команд. */
    @Test
    public void handleText() {
        Message msg = new Message();
        msg.setText("text. Not command");
        Mockito.when(textHandler.handle(msg))
                .thenReturn("отработал текстовый обработчик");
        String handle1 = messageHandler.handle(msg);
        Assert.assertEquals("отработал текстовый обработчик",handle1);

        msg.setText("/command. Not text");
        Mockito.when(commandHandler.handle(msg))
                .thenReturn("отработал командный обработчик");
        String handle2 = messageHandler.handle(msg);
        Assert.assertEquals("отработал командный обработчик",handle2);
    }
}