package core;

import org.junit.Assert;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public class TGMessageHandlerTest {
    @Test
    public void parseTest() {
        MessageHandler tmh = new MessageHandler();
        User user = new User();
        user.setId(0L);
        user.setUserName("JohnDoe");
        Message msg = new Message();
        msg.setText("/start");
        msg.setFrom(user);
        SendMessage sm = tmh.parse(msg);
        Assert.assertEquals("Привет, JohnDoe! Я эхо бот. Отправь /help, чтобы узнать, что я умею", sm.getText());
        msg.setText("/help");
        sm = tmh.parse(msg);
        Assert.assertEquals("Я эхо бот. Отправь мне любой текст и я отправлю тебе его же в ответ.", sm.getText());
        msg.setText("some text need to be echoed");
        sm = tmh.parse(msg);
        Assert.assertEquals("some text need to be echoed", sm.getText());
    }
}
