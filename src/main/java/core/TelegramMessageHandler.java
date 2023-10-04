package core;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Обрабатывает сообщения, поступающие через TelegramBot в методе onUpdateReceived. Возвращает сообщение для отправки.
 */
public class TelegramMessageHandler {
    public SendMessage parse(Message msg){
        SendMessage.SendMessageBuilder smb = SendMessage.builder()
            .chatId(msg.getFrom().getId());
        switch (msg.getText()) {
            case "/greetings", "/start" -> smb.text("Hello!");
            case "/help" -> smb.text("It is a help message.");
            default -> smb.text(msg.getText());
        }
        return smb.build();
    }
}
