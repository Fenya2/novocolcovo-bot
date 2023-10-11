import config.TGBotConfig;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import bot.TGBot;
import bot.Bot;

public class Main {
    public static void main(String[] args) throws TelegramApiException {
        Bot bot = new TGBot(new TGBotConfig("src/main/resources/config/TGBotConfig.json"));
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot((LongPollingBot) bot);
    }
}
