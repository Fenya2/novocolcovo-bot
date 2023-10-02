package ui;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import config.TelegramBotConfig;

public class TelegramBot extends TelegramLongPollingBot implements UserInterface{
    TelegramBotConfig botConfig;
    public TelegramBot(TelegramBotConfig botConfig) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
    }
    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()) {
            Message msg = update.getMessage();
            User user = msg.getFrom();
            switch (msg.getText()) {
                case "/greetings" -> sendTextMessage(user.getId(), "greetings, " + user.getUserName() + "!");
                case "/help" -> sendTextMessage(user.getId(), "help message");
                default -> sendTextMessage(user.getId(), msg.getText());
            }
        }
    }
    @Override
    public void sendTextMessage(Long userID, String text) throws RuntimeException {
        SendMessage sm = SendMessage.builder()
                .chatId(userID.toString())
                .text(text).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}