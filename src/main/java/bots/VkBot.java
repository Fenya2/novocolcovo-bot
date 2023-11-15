package bots;

import api.longpoll.bots.LongPollBot;
import api.longpoll.bots.exceptions.VkApiException;
import api.longpoll.bots.model.events.messages.MessageNew;
import api.longpoll.bots.model.objects.basic.Message;
import config.VkBotConfig;
import core.MessageHandler;
import org.apache.log4j.Logger;

public class VkBot extends LongPollBot implements Bot {
    private static final Logger log = Logger.getLogger(VkBot.class.getName());

    /** @see VkBotConfig */
    private VkBotConfig config;
    private MessageHandler messageHandler;

    /** @param config конфигурация для бота */
    public VkBot(VkBotConfig config, MessageHandler messageHandler) {
        this.config = config;
        this.messageHandler = messageHandler;
    }

    /**
     * срабатывает, когда пользователя отправляет сообщение боту.
     * @param messageNew сообщение, отправленное пользователем.
     */
    @Override
    public void onMessageNew(MessageNew messageNew) {
        Message message = messageNew.getMessage();
        if (message.hasText()) {
            models.Message our_message = new models.Message(message);
            our_message.setBotFrom(this);
            messageHandler.handle(our_message);
        }
        else {
            sendTextMessage(message.getPeerId().toString(),
                    "Сейчас бот работает только с текстом.");
        }
    }

    /**
     * Необходим для работы библиотеки
     * @return
     */
    @Override
    public String getAccessToken() {
        return config.getToken();
    }

    /**
     * Интерфейсный метод. Отправляет текстовое сообщени пользователю с указанным id.
     * @param recipient_id идентификатор пользователя на платформе бота
     * @param message текстовое сообщение для отправки
     */
    @Override
    public void sendTextMessage(String recipient_id, String message) {
        try {
            vk.messages.send()
                    .setPeerId(Integer.parseInt(recipient_id))
                    .setMessage(message)
                    .execute();
        } catch (VkApiException e) {
            log.warn("can't send message with text %s to telegram user with id \"%s\""
                    .formatted(message, recipient_id));
        }
    }

    public String getScreenName(String USER_ID) throws VkApiException{
        return vk.users.get()
                .setUserIds(USER_ID)
                .execute().getResponse().get(0).getScreen_name();
    }
}