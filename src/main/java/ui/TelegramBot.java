package ui;

import core.TelegramMessageHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import config.TelegramBotConfig;

/**
 * Класс для связи с telegram через бота. telegram ui.
 */
public class TelegramBot extends TelegramLongPollingBot implements UserInterface{
    TelegramBotConfig botConfig;
    TelegramMessageHandler tmh;
    public TelegramBot(TelegramBotConfig botConfig) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.tmh = new TelegramMessageHandler();
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