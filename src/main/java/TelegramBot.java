import lombok.NonNull;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import config.BotConfig;

public class TelegramBot extends TelegramLongPollingBot {
    BotConfig botConfig;
    TelegramBot(@NonNull BotConfig botConfig) {
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
                case "/greetings" -> sendMessage(user.getId(), "greetings, " + user.getUserName() + "!");
                case "/help" -> sendMessage(user.getId(), "help message");
            }
        }
    }
    private void sendMessage(Long userID, String text) throws RuntimeException {
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
