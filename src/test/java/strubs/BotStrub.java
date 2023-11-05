package strubs;

import bot.Bot;

public class BotStrub implements Bot {
    @Override
    public void sendTextMessage(String recipient_id, String message) {
        System.out.println("сообщение \"%s\" отправлено пользователю %s!"
                .formatted(message, recipient_id));
    }
}
