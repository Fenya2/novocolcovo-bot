import config.TelegramBotConfig;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ui.TelegramBot;
import ui.UserInterface;

public class Main {
    public static void main(String[] args) throws TelegramApiException {
        UserInterface bot = new TelegramBot(new TelegramBotConfig("src/main/resources/config/TelegramBotConfig.json"));
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot((LongPollingBot) bot);
    }
}
