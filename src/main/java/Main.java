import api.longpoll.bots.exceptions.VkApiException;
import bots.Bot;
import bots.TGBot;
import bots.VkBot;
import config.SQLDBconfig;
import config.TGBotConfig;
import config.VkBotConfig;
import core.CommandHandler;
import core.MessageHandler;
import core.ServiceManager;
import core.UserNotifier;
import core.service_handlers.handlers.*;
import core.service_handlers.services.*;
import db.*;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


import java.sql.SQLException;

/** Main class */
public class Main {
    /** Entry point */
    public static void main(String[] args) throws SQLException, ClassNotFoundException, TelegramApiException, VkApiException, DBException {
        // БД
        DB db = new SQLiteDB(new SQLDBconfig("src/main/resources/config/dbconfig.json"));
        db.connect();
        db.clearScheme();
        db.initScheme();

        // Репозитории
        LoggingUsersRepository loggingUsersRepository = new LoggingUsersRepository(db);
        UserRepository ur = new UserRepository(db);
        LoggedUsersRepository lg = new LoggedUsersRepository(db,ur);
        UserContextRepository uc = new UserContextRepository(db,ur);
        OrderRepository or = new OrderRepository(db,ur);
        UserRateRepository uRateRepository = new UserRateRepository(db);
        
        // Сервисы
        LoginService loginService = new LoginService(uc, loggingUsersRepository, ur, lg);
        RateUserService rateUserService = new RateUserService(uc, uRateRepository);
        EditUserService updateUserService = new EditUserService(uc, ur, rateUserService);
        CreateOrderService createOrderService = new CreateOrderService(or, uc);
        EditOrderService editOrderService = new EditOrderService(or,uc);
        CancelOrderService cancelOrderService = new CancelOrderService(or,uc);
        AcceptOrderService acceptOrderService = new AcceptOrderService(or,uc, ur, lg);
        CloseOrderCourierService closeOrderCourierService = new CloseOrderCourierService(or,uc);
        CloseOrderClientService closeOrderClientService = new CloseOrderClientService(or,uc);

        // менеджер сервисов
        ServiceManager serviceManager = new ServiceManager(lg,or,ur,uc, loginService);

        // Обработчики сервисов
        HandlerLoginService handlerLoginService = new HandlerLoginService(loginService);
        HandlerEditUserService handlerUpdateUserService = new HandlerEditUserService(updateUserService);
        HandlerCreateOrderService handlerCreateOrderService = new HandlerCreateOrderService(createOrderService);
        HandlerEditOrderService handlerEditOrderService = new HandlerEditOrderService(editOrderService);
        HandlerCancelOrderService handlerCancelOrderService = new HandlerCancelOrderService(cancelOrderService);
        HandlerAcceptOrderService handlerAcceptOrderService = new HandlerAcceptOrderService(acceptOrderService);
        HandlerCloseOrderCourierService handlerCloseOrderCourierService =
                new HandlerCloseOrderCourierService(closeOrderCourierService);
        HandlerCloseOrderClientService handlerCloseOrderClientService =
                new HandlerCloseOrderClientService(closeOrderClientService);
        HandlerRateUserService handlerRateUserService = new HandlerRateUserService(rateUserService);


        // "главные" обработчики
        CommandHandler commandHandler = new CommandHandler(serviceManager);
        MessageHandler messageHandler = new MessageHandler(
                loggingUsersRepository,
                uc,
                lg,
                commandHandler,
                handlerLoginService,
                handlerUpdateUserService,
                handlerCreateOrderService,
                handlerEditOrderService,
                handlerCancelOrderService,
                handlerAcceptOrderService,
                handlerCloseOrderCourierService,
                handlerCloseOrderClientService,
                handlerRateUserService
        );

        // Боты
        TGBotConfig tgBotConfig = new TGBotConfig(System.getenv("TG_BOT_TOKEN"), "src/main/resources/config/tgbotconfig.json");
        Bot tgBot = new TGBot(tgBotConfig, messageHandler);
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot((LongPollingBot) tgBot);

        VkBotConfig vkBotConfig = new VkBotConfig(System.getenv("VK_BOT_TOKEN"), "src/main/resources/config/vkbotconfig.json");
        Bot vkBot = new VkBot(vkBotConfig, messageHandler);

        // Доинициализация сервисов для возможности уведомлять пользователей через ботов.
        UserNotifier userNotifier = new UserNotifier(lg,(TGBot) tgBot,(VkBot) vkBot);
        loginService.setUserNotifier(userNotifier);
        closeOrderCourierService.setUserNotifier(userNotifier);
        acceptOrderService.setUserNotifier(userNotifier);
        closeOrderClientService.setUserNotifier(userNotifier);

        ((VkBot) vkBot).startPolling();
        db.disconnect();
    }
}
