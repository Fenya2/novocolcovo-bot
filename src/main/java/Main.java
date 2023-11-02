import bot.Bot;
import bot.TGBot;
import config.SQLiteDBconfig;
import config.TGBotConfig;
import db.*;
import new_core.handlers.CommandHandler;
import new_core.handlers.MessageHandler;
import new_core.handlers.service_handlers.UpdateUserServiceHandler;
import new_core.services.UpdateUserService;
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

        UpdateUserService updateUserService = new UpdateUserService(uc, ur);
        UpdateUserServiceHandler updateUserServiceHandler = new UpdateUserServiceHandler(updateUserService);
        CommandHandler commandHandler = new CommandHandler(updateUserService);
        MessageHandler messageHandler = new MessageHandler(uc, lg, commandHandler, updateUserServiceHandler);

        TGBotConfig tgBotConfig = new TGBotConfig("src/main/resources/config/TGBotConfig.json");

        Bot bot = new TGBot(tgBotConfig, messageHandler);
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot((LongPollingBot) bot);
    }
}
