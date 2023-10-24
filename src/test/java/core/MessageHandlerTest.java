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
        Mockito.when(textHandler.handle(msg))
                .thenReturn("отработал текстовый обработчик");
        String handel1 = messageHandler.handle(msg);
        Assertions.assertEquals("отработал текстовый обработчик",handel1);

        msg.setText("/command. Not text");
        Mockito.when(commandHandler.handle(msg))
                .thenReturn("отработал командный обработчик");
        String handel2 = messageHandler.handle(msg);
        Assertions.assertEquals("отработал командный обработчик",handel2);
    }
}