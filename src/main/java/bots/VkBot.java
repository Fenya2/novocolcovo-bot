package bots;

import api.longpoll.bots.LongPollBot;
import api.longpoll.bots.exceptions.VkApiException;
import api.longpoll.bots.model.events.messages.MessageNew;
import api.longpoll.bots.model.objects.basic.Message;
import com.google.gson.JsonObject;
import config.VkBotConfig;
import core.MessageHandler;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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

    /**
     * Заглушка
     */
    @Override
    public void sendMainMenu(String recipient_id, String message) {
        //sendTextMessage(recipient_id,message);
    }

    /**
     * Возвращает доменное имя пользователя на платформе, по которому его легко найти. Если
     * какая-то ошибка - null (cm лог.)
     */
    public String getDomainByUserIdOnPlatform(String userIdOnPlatform) {
        URL url;
        JSONObject jo;
        try {
            url = new URL(
                    config.getApiMethods().get("getDomainByUserIdOnPlatform")
                            .formatted(config.getToken(), userIdOnPlatform)
            );
            URLConnection con = url.openConnection();
            HttpsURLConnection https = (HttpsURLConnection) con;
            https.setRequestMethod("POST");
            jo = new JSONObject(IOUtils.toString(url, StandardCharsets.UTF_8));
            return jo.getJSONArray("response").getJSONObject(0).getString("domain");
        } catch (MalformedURLException e) {
            log.error("Неправильный url\n%s".formatted(e.getMessage()));
            return null;
        } catch (IOException e) {
            log.error("Ошибка ввода-вывода\n%s".formatted(e.getMessage()));
            return null;
        }
    }
}