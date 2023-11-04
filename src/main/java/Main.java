import bot.Bot;
import bot.TGBot;
import config.SQLiteDBconfig;
import config.TGBotConfig;
import db.*;
import new_core.service_handlers.handlers.*;
import new_core.MessageHandler;
import new_core.service_handlers.services.*;
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

        ServiceManager serviceManager = new ServiceManager(lg,or,ur,uc);
        EditUserService updateUserService = new EditUserService(uc, ur);
        CreateOrderService createOrderService = new CreateOrderService(or, uc);
        EditOrderService editOrderService = new EditOrderService(or,uc);
        CancelOrderService cancelOrderService = new CancelOrderService(or,uc);
        CloseOrderService closeOrderService = new CloseOrderService(or,uc);
        AcceptOrderService acceptOrderService = new AcceptOrderService(or,uc);

        HandlerEditUserService handlerUpdateUserService = new HandlerEditUserService(updateUserService);
        HandlerCreateOrderService handlerCreateOrderService = new HandlerCreateOrderService(createOrderService);
        HandlerEditOrderService handlerEditOrderService = new HandlerEditOrderService(editOrderService);
        HandlerCancelOrderService handlerCancelOrderService = new HandlerCancelOrderService(cancelOrderService);
        HandlerAcceptOrderService handlerAcceptOrderService = new HandlerAcceptOrderService(acceptOrderService);
        HandlerCloseOrderService handlerCloseOrderService = new HandlerCloseOrderService(closeOrderService);

        CommandHandler commandHandler = new CommandHandler(serviceManager);
        MessageHandler messageHandler = new MessageHandler(uc, lg,
                commandHandler,
                handlerUpdateUserService,
                handlerCreateOrderService,
                handlerEditOrderService,
                handlerCancelOrderService,
                handlerAcceptOrderService,
                handlerCloseOrderService);

        TGBotConfig tgBotConfig = new TGBotConfig("src/main/resources/config/TGBotConfig.json");

        Bot bot = new TGBot(tgBotConfig, messageHandler);
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot((LongPollingBot) bot);
    }
}
