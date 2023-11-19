package core;

import db.DBException;
import db.LoggedUsersRepository;
import db.LoggingUsersRepository;
import db.UserContextRepository;
import models.Domain;
import models.Message;
import models.User;
import models.UserContext;

import core.service_handlers.handlers.*;

import java.sql.SQLException;

/**
 * Класс, обрабатывающий сообщения, поступающие от пользователей из разных платформ, направляющий
 * сообщение в нужный сервисный обработчике, в зависимости от контекста пользователя для последующей
 * обработки
 */
public class MessageHandler {

    private final LoggingUsersRepository loggingUsersRepository;
    /** @see UserContextRepository */
    private final UserContextRepository userContextRepository;

    /** @see LoggedUsersRepository*/
    private final LoggedUsersRepository loggedUsersRepository;

    /** @see CommandHandler */
    private final CommandHandler commandHandler;

    private final HandlerLoginService handlerLoginService;

    /** @see HandlerEditUserService */
    private final HandlerEditUserService handlerEditUserService;

    /** @see HandlerCreateOrderService */
    private final HandlerCreateOrderService handlerCreateOrderService;

    /** @see HandlerEditOrderService */
    private final HandlerEditOrderService handlerEditOrderService;

    /** @see HandlerCancelOrderService */
    private final HandlerCancelOrderService handlerCancelOrderService;

    /** @see HandlerAcceptOrderService */
    private final HandlerAcceptOrderService handlerAcceptOrderService;

    /** @see HandlerCloseOrderCourierService */
    private final HandlerCloseOrderCourierService handlerCloseOrderCourierService;

    /** @see HandlerCloseOrderClientService */
    private final HandlerCloseOrderClientService handlerCloseOrderClientService;

    /** Конструктор {@link MessageHandler MessageHandler}*/
    public MessageHandler(
            LoggingUsersRepository loggingUsersRepository,
            UserContextRepository userContextRepository,
            LoggedUsersRepository loggedUsersRepository,
            CommandHandler commandHandler,
            HandlerLoginService handlerLoginService,
            HandlerEditUserService updateUserServiceHandler,
            HandlerCreateOrderService handlerCreateOrderService,
            HandlerEditOrderService handlerEditOrderService,
            HandlerCancelOrderService handlerCancelOrderService,
            HandlerAcceptOrderService handlerAcceptOrderService,
            HandlerCloseOrderCourierService handlerCloseOrderService,
            HandlerCloseOrderClientService handlerCloseOrderClientService) {
        this.loggingUsersRepository = loggingUsersRepository;
        this.userContextRepository = userContextRepository;
        this.loggedUsersRepository = loggedUsersRepository;
        this.commandHandler = commandHandler;
        this.handlerLoginService = handlerLoginService;
        this.handlerEditUserService = updateUserServiceHandler;
        this.handlerCreateOrderService = handlerCreateOrderService;
        this.handlerEditOrderService = handlerEditOrderService;
        this.handlerCancelOrderService = handlerCancelOrderService;
        this.handlerAcceptOrderService = handlerAcceptOrderService;
        this.handlerCloseOrderCourierService = handlerCloseOrderService;
        this.handlerCloseOrderClientService = handlerCloseOrderClientService;
    }

    /**
     * Первичный метод обработки сообщения. Если у пользователя, отправившего сообщение есть
     * контекст, направляет сообщение в соответствующий сервисный обработчик, если отправленное
     * сообщение является командой, перенаправляет сообщение в обработчик команд.
     * <p>
     * Если у пользователя нет контекста, но он находится в состоянии авторизации,
     * то есть в таблице logging_users, то перенаправляет сообщение в обработчик авторизации.
     * <p>
     * Иначе сообщает
     * пользователю, что сообщение некорректно.
     * @return 1, если сообщение написано пользователем впервые. 2, если пользователь пишет, не
     * находясь в контексте. 3, если пользователь пишет, находясь в контексте.
     */
    public int handle(Message msg) {
        try {
            Domain domain = loggingUsersRepository.getDomainByFromPlatformAndIdOnPlatform(
                    msg.getPlatform(),
                    msg.getUserIdOnPlatform()
            );
            if(domain != null) {
                handlerLoginService.handle(msg, domain);
                return 4;
            }
        } catch (DBException e) {
            msg.getBotFrom().sendTextMessage(
                    msg.getUserIdOnPlatform(),
                    "Проблемы с доступом к базе данных " + e.getMessage()
            );
            return -1;
        }

        User user;
        UserContext userContext;
        try {
            user = loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                    msg.getPlatform(),
                    msg.getUserIdOnPlatform()
            );
        } catch (SQLException | DBException e) {
            msg.getBotFrom().sendTextMessage(
                    msg.getUserIdOnPlatform(),
                    "Проблемы с доступом к базе данных " + e.getMessage()
            );
            return -1;
        }

        if(user == null) {
            switch (msg.getText()) {
                case "/start", "/register", "/login": {
                    commandHandler.handle(msg);
                    return 1;
                }
                case "/help": {
                    msg.getBotFrom().sendTextMessage(
                            msg.getUserIdOnPlatform(),
                            "/register - зарегистрироваться в системе.\n" +
                                    "/login - войти"
                    );
                    return 1;
                }
            }
            msg.getBotFrom().sendTextMessage(
                    msg.getUserIdOnPlatform(),
                    """
                            Для пользования ботом тебе нужно зарегистрироваться или войти.
                            /register - зарегистрироваться в системе.
                            /login - войти"""
            );
            return 1;
        }

        msg.setUser(user);
        try {userContext = userContextRepository.getUserContext(user.getId());}
        catch (SQLException e) {
            msg.getBotFrom().sendTextMessage(
                    msg.getUserIdOnPlatform(),
                    "Проблемы с доступом к базе данных " + e.getMessage());
            return -1;
        }

        msg.setUserContext(userContext);
        switch(userContext.getState()) {
            case NO_STATE -> {
                commandHandler.handle(msg);
                return 2;
            }
            case EDIT_USER -> handlerEditUserService.handle(msg);
            case ORDER_CREATING -> handlerCreateOrderService.handle(msg);
            case ORDER_EDITING -> handlerEditOrderService.handle(msg);
            case ORDER_CANCELING-> handlerCancelOrderService.handle(msg);
            case ORDER_ACCEPT -> handlerAcceptOrderService.handle(msg);
            case ORDER_CLOSING_COURIER -> handlerCloseOrderCourierService.handle(msg);
            case ORDER_CLOSING_CLIENT -> handlerCloseOrderClientService.handle(msg);
        }
        return 3;
    }
}
