import bot.Bot;
import bot.TGBot;
import config.SQLiteDBconfig;
import config.Stickers;
import config.TGBotConfig;
import core.CommandHandler;
import core.MessageHandler;
import core.TextHandler;
import core.service.OrderService;
import db.*;
import models.Platform;
import models.User;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


import java.sql.SQLException;

/** Main class */
public class Main {
    /** Entry point */
    public static void main(String[] args) throws SQLException, ClassNotFoundException, TelegramApiException {
        DB db = new SQLiteDB(new SQLiteDBconfig("src/main/resources/config/dbconfig.json"));
        UserRepository ur = new UserRepository(db);
        LoggedUsersRepository lg = new LoggedUsersRepository(db,ur);
        UserContextRepository uc = new UserContextRepository(db,ur);
        OrderRepository or = new OrderRepository(db,ur);

        OrderService os = new OrderService(uc, or);

        CommandHandler cm = new CommandHandler(ur,lg, uc, os);
        TextHandler th = new TextHandler(lg, uc, os);
        MessageHandler mh = new MessageHandler(cm, th);

        TGBotConfig tgBotConfig = new TGBotConfig("src/main/resources/config/TGBotConfig.json");

        Bot bot = new TGBot(tgBotConfig,mh);
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot((LongPollingBot) bot);
    }
}