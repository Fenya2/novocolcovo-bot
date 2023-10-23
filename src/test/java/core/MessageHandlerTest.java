package core;

import models.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class MessageHandlerTest {
    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }
    @InjectMocks
    private MessageHandler messageHandler;
    @Mock
    private TextHandler textHandler;
    @Mock
    private CommandHandler commandHandler;

    @Test
    void handleText() {
        Message msg = new Message();
        msg.setText("text. Not command");
        Mockito.when(textHandler.handle(msg)).thenReturn("отработал текстовый обработчик");
        Mockito.when(commandHandler.handle(msg)).thenReturn("отработал командный обработчик");
        String handel = messageHandler.handle(msg);
        Assertions.assertEquals("отработал текстовый обработчик",handel);
    }
    @Test
    void handleCommand() {
        Message msg = new Message();
        msg.setText("/command. Not text");
        Mockito.when(textHandler.handle(msg)).thenReturn("отработал текстовый обработчик");
        Mockito.when(commandHandler.handle(msg)).thenReturn("отработал командный обработчик");
        String handel = messageHandler.handle(msg);
        Assertions.assertEquals("отработал командный обработчик",handel);
    }
}