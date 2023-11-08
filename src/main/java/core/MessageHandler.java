package core;

import db.LoggedUsersRepository;
import db.UserContextRepository;
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
    /** @see UserContextRepository */
    private final UserContextRepository userContextRepository;

    /** @see LoggedUsersRepository*/
    private final LoggedUsersRepository loggedUsersRepository;

    /** @see CommandHandler */
    private final CommandHandler commandHandler;

    /** @see HandlerEditUserService */
    private final HandlerEditUserService handlerEditUserService;

    /** @see HandlerCreateOrderService */
    private final HandlerCreateOrderService handlerCreateOrderService;

    /** @see HandlerEditOrderService */
    private final HandlerEditOrderService handlerEditOrderService;

    /** @see HandlerCancelOrderService */
    private final HandlerCancelOrderService handlerCancelOrderService;

    /** @see HandlerAcceptOrderCourierService*/
    private final HandlerAcceptOrderCourierService handlerAcceptOrderCourierService;

    /** @see HandlerCloseOrderCourierService */
    private final HandlerCloseOrderCourierService handlerCloseOrderCourierService;

    /** @see HandlerCloseOrderClientService */
    private final HandlerCloseOrderClientService handlerCloseOrderClientService;

    /** @see HandlerAcceptOrderClientService*/
    private final HandlerAcceptOrderClientService handlerAcceptOrderClientService;


    /** Конструктор {@link MessageHandler MessageHandler}*/
    public MessageHandler(
            UserContextRepository userContextRepository,
            LoggedUsersRepository loggedUsersRepository,
            CommandHandler commandHandler,
            HandlerEditUserService updateUserServiceHandler,
            HandlerCreateOrderService handlerCreateOrderService,
            HandlerEditOrderService handlerEditOrderService,
            HandlerCancelOrderService handlerCancelOrderService,
            HandlerAcceptOrderCourierService handlerAcceptOrderService,
            HandlerAcceptOrderClientService handlerAcceptOrderClientService,
            HandlerCloseOrderCourierService handlerCloseOrderService,
            HandlerCloseOrderClientService handlerCloseOrderClientService
    ) {
        this.userContextRepository = userContextRepository;
        this.loggedUsersRepository = loggedUsersRepository;
        this.commandHandler = commandHandler;
        this.handlerEditUserService = updateUserServiceHandler;
        this.handlerCreateOrderService = handlerCreateOrderService;
        this.handlerEditOrderService = handlerEditOrderService;
        this.handlerCancelOrderService = handlerCancelOrderService;
        this.handlerAcceptOrderCourierService = handlerAcceptOrderService;
        this.handlerAcceptOrderClientService = handlerAcceptOrderClientService;
        this.handlerCloseOrderCourierService = handlerCloseOrderService;
        this.handlerCloseOrderClientService = handlerCloseOrderClientService;
    }

    /**
     * Первичный метод обработки сообщения. Если у пользователя, отправившего сообщение есть
     * контекст, направляет сообщение в соответствующий сервисный обработчик, если отправленное
     * сообщение является командой, перенаправляет сообщение в обработчик команд. Иначе сообщает
     * пользователю, что сообщение некорректно.
     * @return 1, если сообщение написано пользователем впервые. 2, если пользователь пишет, не
     * находясь в контексте. 3, если пользователь пишет, находясь в контексте.
     */
    public int handle(Message msg) {
        User user;
        UserContext userContext;
        try {
            user = loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                    msg.getPlatform(),
                    msg.getUserIdOnPlatform()
            );
        } catch (SQLException e) {
            msg.getBotFrom().sendTextMessage(
                    msg.getUserIdOnPlatform(),
                    "Проблемы с доступом к базе данных " + e.getMessage()
            );
            return -1;
        }

        if(user == null) {
            if (msg.getText().equals("/start")) {
                commandHandler.handle(msg);
                return 1;
            }
            msg.getBotFrom().sendTextMessage(
                    msg.getUserIdOnPlatform(),
                    "отправьте /start для последующей работы."
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
            case ORDER_ACCEPTING_COURIER -> handlerAcceptOrderCourierService.handle(msg);
            case ORDER_ACCEPTING_CLIENT -> handlerAcceptOrderClientService.handle(msg);
            case ORDER_CLOSING_COURIER -> handlerCloseOrderCourierService.handle(msg);
            case ORDER_CLOSING_CLIENT -> handlerCloseOrderClientService.handle(msg);
        }
        return 3;
    }
}
