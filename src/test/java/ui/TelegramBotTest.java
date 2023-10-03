package ui;

import config.TelegramBotConfig;
import org.junit.Test;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TelegramBotTest {
    @Test
    public void registerBotTest() throws TelegramApiException {
        UserInterface bot = new TelegramBot(new TelegramBotConfig("src/main/resources/config/TelegramBotConfig.json"));
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot((LongPollingBot) bot);
    }

    @Test
    public void sendMessageTest() throws TelegramApiException {
        UserInterface bot = new TelegramBot(new TelegramBotConfig("src/main/resources/config/TelegramBotConfig.json"));
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot((LongPollingBot) bot);
        bot.sendTextMessage(517043435L, "It's test message");
    }
}
