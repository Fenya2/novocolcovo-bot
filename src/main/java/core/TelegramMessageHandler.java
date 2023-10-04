package core;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class TelegramMessageHandler {
    public SendMessage parse(Message msg){
        Long userID = msg.getFrom().getId();
        String text = msg.getText();
        SendMessage sm = SendMessage.builder()
        .chatId(userID.toString()).text(text).build();
        switch (msg.getText()) {
            case "/greetings" -> sm.setText("Hello!");
            case "/help" -> sm.setText("");
            default -> sm.setText(text);
        }
        return sm;
    }
}
