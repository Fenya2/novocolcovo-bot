import api.longpoll.bots.exceptions.VkApiException;
import bots.Bot;
import bots.TGBot;
import bots.VkBot;
import config.SQLDBconfig;
import config.TGBotConfig;
import config.VkBotConfig;
import core.MessageHandler;
import core.UserNotifier;
import core.service_handlers.handlers.*;
import core.service_handlers.services.*;
import db.*;
import core.ServiceManager;
import core.CommandHandler;

import models.Domain;
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

        LoggingUsersRepository loggingUsersRepository = new LoggingUsersRepository(db);
        loggingUsersRepository.saveDomain(new Domain());

//        // Репозитории
//        UserRepository ur = new UserRepository(db);
//        LoggedUsersRepository lg = new LoggedUsersRepository(db,ur);
//        UserContextRepository uc = new UserContextRepository(db,ur);
//        OrderRepository or = new OrderRepository(db,ur);
//
//        // Сервисы
//        ServiceManager serviceManager = new ServiceManager(lg,or,ur,uc);
//        EditUserService updateUserService = new EditUserService(uc, ur);
//        CreateOrderService createOrderService = new CreateOrderService(or, uc);
//        EditOrderService editOrderService = new EditOrderService(or,uc);
//        CancelOrderService cancelOrderService = new CancelOrderService(or,uc);
//        AcceptOrderService acceptOrderService = new AcceptOrderService(or,uc);
//        CloseOrderCourierService closeOrderCourierService = new CloseOrderCourierService(or,uc);
//        CloseOrderClientService closeOrderClientService = new CloseOrderClientService(or,uc);
//
//        // Обработчики сервисов
//        HandlerEditUserService handlerUpdateUserService = new HandlerEditUserService(updateUserService);
//        HandlerCreateOrderService handlerCreateOrderService = new HandlerCreateOrderService(createOrderService);
//        HandlerEditOrderService handlerEditOrderService = new HandlerEditOrderService(editOrderService);
//        HandlerCancelOrderService handlerCancelOrderService = new HandlerCancelOrderService(cancelOrderService);
//        HandlerAcceptOrderService handlerAcceptOrderService = new HandlerAcceptOrderService(acceptOrderService);
//        HandlerCloseOrderCourierService handlerCloseOrderCourierService =
//                new HandlerCloseOrderCourierService(closeOrderCourierService);
//        HandlerCloseOrderClientService handlerCloseOrderClientService =
//                new HandlerCloseOrderClientService(closeOrderClientService);
//
//        // "главные" обработчики
//        CommandHandler commandHandler = new CommandHandler(serviceManager);
//        MessageHandler messageHandler = new MessageHandler(
//                uc,
//                lg,
//                commandHandler,
//                handlerUpdateUserService,
//                handlerCreateOrderService,
//                handlerEditOrderService,
//                handlerCancelOrderService,
//                handlerAcceptOrderService,
//                handlerCloseOrderCourierService,
//                handlerCloseOrderClientService
//        );
//
//        // Боты
//        TGBotConfig tgBotConfig = new TGBotConfig(System.getenv("TG_BOT_TOKEN"));
//        Bot tgBot = new TGBot(tgBotConfig, messageHandler);
//        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//        botsApi.registerBot((LongPollingBot) tgBot);
//
//        VkBotConfig vkBotConfig = new VkBotConfig(System.getenv("VK_BOT_TOKEN"));
//        Bot vkBot = new VkBot(vkBotConfig, messageHandler);
//
//        UserNotifier userNotifier = new UserNotifier(lg, tgBot, vkBot);
//        closeOrderCourierService.setMessageSender(userNotifier);
//        acceptOrderService.setMessageSender(userNotifier);
//        closeOrderClientService.setMessageSender(userNotifier);
//
//        ((VkBot) vkBot).startPolling();
//
//        db.disconnect();
    }
}
