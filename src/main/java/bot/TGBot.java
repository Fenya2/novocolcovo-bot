package bot;

import core.CommandHandler;
import core.MessageHandler;
import core.TextHandler;
import db.UserRepository;
import models.Message;
import models.User;
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
            try {
                String text = update.getMessage().getText();
                String id = update.getMessage().getChatId().toString();

                Message message = new Message();
                message.setText(text);
                message.setPlatform(botConfig.getPlatform());
                message.setUserIdOnPlatform(id);

                String msg = messageHandler.handle(message);

                SendMessage sendMes = new SendMessage();
                sendMes.setText(msg);
                sendMes.setChatId(id);
                execute(sendMes);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public void sendTextMessage(long recipient_id, String text) {
    }
}