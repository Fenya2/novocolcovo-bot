package core;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Обрабатывает сообщения, поступающие через TelegramBot в методе onUpdateReceived. Возвращает сообщение для отправки.
 */
public class MessageHandler {
    public SendMessage parse(Message msg){
        SendMessage.SendMessageBuilder smb = SendMessage.builder()
            .chatId(msg.getFrom().getId());
        switch (msg.getText()) {
            case "/start" -> smb.text(String.format("Привет, %s! Я эхо бот. Отправь /help, чтобы узнать, что я умею", msg.getFrom().getUserName()));
            case "/help" -> smb.text("Я эхо бот. Отправь мне любой текст и я отправлю тебе его же в ответ.");
            default -> smb.text(msg.getText());
        }
        return smb.build();
    }

}
