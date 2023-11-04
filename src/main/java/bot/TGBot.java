package bot;

import models.Message;
import core.MessageHandler;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import config.TGBotConfig;

/** Класс для связи с telegram через бота. */
public class TGBot extends TelegramLongPollingBot implements Bot {
    private static final Logger log = Logger.getLogger(TGBot.class.getName());
    /** Конфигурация для работы с ботом. */
    TGBotConfig botConfig;
    /** Обработчик сообщений */
    MessageHandler messageHandler;

    /**
     * @param botConfig объект конфигурации бота
     * @param messageHandler обработчик сообщений
     */
    public TGBot(TGBotConfig botConfig, MessageHandler messageHandler) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.messageHandler = messageHandler;
    }

    /** Возвращает имя бота, указанное в конфигурационном файле бота. */
    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    /** Метод, вызывающийся, когда пользователь как-то взаимодействует с ботом. */
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()) {
            if(update.getMessage().hasText()) {
                Message message = new Message(update.getMessage());
                message.setBotFrom(this);
                messageHandler.handle(message);
            }
            else {
                sendTextMessage(update.getMessage().getChatId().toString(),
                        "Сейчас бот работает только с текстом.");
            }
        } else {
            // todo обработка другого рода обновлений.
        }
    }

    /**
     * Отправляет сообщение пользователю по его идентификатору на платформе.
     * @param userIdOnPlatform идентификатор пользователя на платформе. Не <b>null</b>
     * @param text текст сообщения. Не <b>null</b>
     */
    @Override
    public void sendTextMessage(String userIdOnPlatform, String text) throws IllegalArgumentException {
        if(userIdOnPlatform == null)
            throw new IllegalArgumentException("userIdOnPlatform must be not null.");
        if(text == null)
            throw new IllegalArgumentException("text must be not null");
        SendMessage sendMessage = SendMessage.builder()
                .text(text)
                .chatId(userIdOnPlatform)
                .build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.warn("can't send message with text %s to telegram user with id \"%s\""
                    .formatted(text, userIdOnPlatform));
        }
    }

    public void sendSticker(String recipientId, String stickerToken) throws TelegramApiException {
        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(recipientId);
        sendSticker.setSticker(new InputFile(stickerToken));
        try {
            execute(sendSticker);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
