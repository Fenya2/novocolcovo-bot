import bot.Bot;
import bot.TGBot;
import config.SQLiteDBconfig;
import config.TGBotConfig;
import db.*;
import new_core.handlers.CommandHandler;
import new_core.handlers.MessageHandler;
import new_core.handlers.service_handlers.EditUserServiceHandler;
import new_core.services.EditUserService;
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

        EditUserService editUserService = new EditUserService(uc, ur);
        EditUserServiceHandler editUserServiceHandler = new EditUserServiceHandler(editUserService);
        CommandHandler commandHandler = new CommandHandler(editUserService);
        MessageHandler messageHandler = new MessageHandler(uc, lg, commandHandler, editUserServiceHandler);

        TGBotConfig tgBotConfig = new TGBotConfig(args[0]);

        Bot bot = new TGBot(tgBotConfig, messageHandler);
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot((LongPollingBot) bot);
    }
}
