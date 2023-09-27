import config.BotConfig;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws TelegramApiException{
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        BotConfig bc;
        try {
            bc = new BotConfig("src/main/resources/telegram_bot.conf");
        } catch (FileNotFoundException e) {
            System.err.println("config file not found");
            return;
        }
        TelegramBot bot = new TelegramBot(bc);
        botsApi.registerBot(bot);
    }
}
