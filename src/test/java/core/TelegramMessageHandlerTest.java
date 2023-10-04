package core;

import org.junit.Assert;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public class TelegramMessageHandlerTest {
    @Test
    public void parseTest() {
        TelegramMessageHandler tmh = new TelegramMessageHandler();
        User user = new User();
        user.setId(0L);
        Message msg = new Message();
        msg.setText("/start");
        msg.setFrom(user);
        SendMessage sm = tmh.parse(msg);
        Assert.assertEquals(sm.getText(), "Hello!");
        msg.setText("/greetings");
        sm = tmh.parse(msg);
        Assert.assertEquals(sm.getText(), "Hello!");
        msg.setText("/help");
        sm = tmh.parse(msg);
        Assert.assertEquals(sm.getText(), "It is a help message.");
    }
}
