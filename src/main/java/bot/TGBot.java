package bot;

import core.MessageHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import config.TGBotConfig;

/**
 * Класс для связи с telegram через бота. telegram ui.
 */
public class TGBot extends TelegramLongPollingBot implements Bot {
    TGBotConfig botConfig;
    MessageHandler tmh;
    public TGBot(TGBotConfig botConfig) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.tmh = new MessageHandler();
    }
    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()) {
            try {
                execute(tmh.parse(update.getMessage()));
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public void sendTextMessage(long recipient_id, String text) {
    }
}