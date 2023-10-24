package bot;

import core.CommandHandler;
import core.MessageHandler;
import core.TextHandler;
import db.UserRepository;
import models.Message;
import models.User;
import org.apache.log4j.Logger;
import org.sqlite.core.DB;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import config.TGBotConfig;

import java.sql.SQLException;

/**
 * Класс для связи с telegram через бота. .
 */
public class TGBot extends TelegramLongPollingBot implements Bot {
    private static final Logger log = Logger.getLogger(TGBot.class.getName());
    /**
     * Конфигурации для работы с ботом
     */
    TGBotConfig botConfig;
    /**
     * Обработчик сообщений
     */
    MessageHandler messageHandler;
    /**
     * Конструктор TGBot
     */
    public TGBot(TGBotConfig botConfig,MessageHandler messageHandler) throws SQLException {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.messageHandler = messageHandler;
    }
    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    /**
     * Связь с ботом
     */
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()) {
            if(update.getMessage().hasText()) {
                Message message = new Message(update.getMessage());
                String text = messageHandler.handle(message);
                sendTextMessage(String.valueOf(update.getMessage().getChatId()), text);
            }
            else {
               sendTextMessage(update.getMessage().getChatId().toString(),
                       "Сейчас бот работает только с текстом.");
            }
        } else {
            // todo обработка другого рода обновлений.
        }
    }
    @Override
    public void sendTextMessage(String chatId, String text) {
        SendMessage sendMessage = SendMessage.builder()
                .text(text)
                .chatId(chatId)
                .build();

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.warn("can't send message with text %s to telegram user with id \"%s\""
                    .formatted(text, chatId));
        }
    }
}