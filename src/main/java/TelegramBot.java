import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramBot extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return "Test";
    }

    @Override
    public String getBotToken() {
        return "YOUR TOKEN HERE";
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("test");
    }
}
