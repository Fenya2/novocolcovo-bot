package bots;

import core.MessageHandler;
import models.Message;
import models.Platform;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import config.TGBotConfig;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/** Класс для связи с telegram через бот. */
public class TGBot extends TelegramLongPollingBot implements Bot {

    private static final Logger log = Logger.getLogger(TGBot.class.getName());
    /** Конфигурация для работы с ботом. */
    TGBotConfig config;
    /** Обработчик сообщений */
    MessageHandler messageHandler;

    /**
     * @param botConfig объект конфигурации бота
     * @param messageHandler обработчик сообщений
     */
    public TGBot(TGBotConfig botConfig, MessageHandler messageHandler) {
        super(botConfig.getToken());
        this.config = botConfig;
        this.messageHandler = messageHandler;
    }

    /** Возвращает имя бота, указанное в конфигурационном файле бота. */
    @Override
    public String getBotUsername() {
        return config.getName();
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
        }
        else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            Message message = new Message();
            message.setPlatform(Platform.TELEGRAM);
            message.setUserIdOnPlatform(String.valueOf(chatId));
            message.setBotFrom(this);

            switch (callbackData){
                case "PROFILE_BUTTON" -> {
                    message.setText("/profile");
                    messageHandler.handle(message);
                }
                case "CREATE_BUTTON" -> {
                    message.setText("/create_order");
                    messageHandler.handle(message);
                }
                case "EDIT_BUTTON" -> {
                    message.setText("/edit_order");
                    messageHandler.handle(message);
                }
                case "CANCEL_BUTTON" -> {
                    message.setText("/cancel_order");
                    messageHandler.handle(message);
                }
                case "SHOW_ORDER_BUTTON" -> {
                    message.setText("/show_order");
                    messageHandler.handle(message);
                }
                case "SHOW_PENDING_BUTTON" -> {
                    message.setText("/show_pending_orders");
                    messageHandler.handle(message);
                }
                case "ACCEPT_BUTTON" -> {
                    message.setText("/accept_order");
                    messageHandler.handle(message);
                }
                case "SHOW_ACCEPT_BUTTON" -> {
                    message.setText("/show_accept_order");
                    messageHandler.handle(message);
                }
                case "CLOSE_BUTTON" -> {
                    message.setText("/close_order");
                    messageHandler.handle(message);
                }
            }
        }
        else {
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

    @Override
    public void sendMainMenu(String chatId, String msg) {

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(msg);

        InlineKeyboardButton profileButton = new InlineKeyboardButton();
        profileButton.setText("Мой профиль");
        profileButton.setCallbackData("PROFILE_BUTTON");

        InlineKeyboardButton createButton = new InlineKeyboardButton();
        createButton.setText("Создать заказ");
        createButton.setCallbackData("CREATE_BUTTON");

        InlineKeyboardButton editButton = new InlineKeyboardButton();
        editButton.setText("Изменить заказ");
        editButton.setCallbackData("EDIT_BUTTON");

        InlineKeyboardButton cancelButton = new InlineKeyboardButton();
        cancelButton.setText("Отменить заказ");
        cancelButton.setCallbackData("CANCEL_BUTTON");

        InlineKeyboardButton showOrderButton = new InlineKeyboardButton();
        showOrderButton.setText("Мои заказы");
        showOrderButton.setCallbackData("SHOW_ORDER_BUTTON");

        InlineKeyboardButton showPendingOrderButton = new InlineKeyboardButton();
        showPendingOrderButton.setText("Все заказы");
        showPendingOrderButton.setCallbackData("SHOW_PENDING_BUTTON");

        InlineKeyboardButton acceptOrderButton = new InlineKeyboardButton();
        acceptOrderButton.setText("Принять заказ");
        acceptOrderButton.setCallbackData("ACCEPT_BUTTON");

        InlineKeyboardButton showAcceptOrderButton = new InlineKeyboardButton();
        showAcceptOrderButton.setText("Принятые заказы");
        showAcceptOrderButton.setCallbackData("SHOW_ACCEPT_BUTTON");

        InlineKeyboardButton closeOrderButton = new InlineKeyboardButton();
        closeOrderButton.setText("Завершить заказ");
        closeOrderButton.setCallbackData("CLOSE_BUTTON");


        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine3 = new ArrayList<>();

        rowInLine1.add(createButton);
        rowInLine1.add(editButton);
        rowInLine1.add(cancelButton);
        rowsInLine.add(rowInLine1);

        rowInLine2.add(profileButton);
        rowInLine2.add(showOrderButton);
        rowInLine2.add(showPendingOrderButton);
        rowsInLine.add(rowInLine2);

        rowInLine3.add(acceptOrderButton);
        rowInLine3.add(showAcceptOrderButton);
        rowInLine3.add(closeOrderButton);
        rowsInLine.add(rowInLine3);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * По id пользователя на платформе возвращает его домен (тег)
     * @param userIdOnPlatform
     * @return
     */
    public String getDomainByUserIdOnPlatform(String userIdOnPlatform) {
        URL url;
        JSONObject jo = null;
        try {
            url = new URL(
                    config.getApiMethods().get("getDomainByUserIdOnPlatform")
                            .formatted(config.getToken(), userIdOnPlatform, userIdOnPlatform)
            );
            URLConnection con = url.openConnection();
            HttpsURLConnection https = (HttpsURLConnection) con;
            https.setRequestMethod("POST");
            jo = new JSONObject(IOUtils.toString(url, StandardCharsets.UTF_8));
            return jo.getJSONObject("result").getJSONObject("user").getString("username");
        } catch (MalformedURLException e) {
            log.error("Неправильный url\n%s".formatted(e.getMessage()));
        } catch (IOException e) {
            log.error("Ошибка ввода-вывода\n%s".formatted(e.getMessage()));
        } catch (Exception e) {
            log.error("Непредвиденная ошибка. Возможно у пользователя скрыт username\n%s"
                    .formatted(
                    e.getMessage()));
        }
        return null;
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
