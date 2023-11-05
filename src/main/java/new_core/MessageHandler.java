package new_core;

import db.LoggedUsersRepository;
import db.UserContextRepository;
import models.Message;
import models.User;
import models.UserContext;
import new_core.service_handlers.handlers.*;

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
    private final HandlerAcceptOrderService handlerAcceptOrderService;
    private final HandlerCloseOrderCourierService handlerCloseOrderCourierService;
    private final HandlerCloseOrderClientService handlerCloseOrderClientService;


    /** Конструктор {@link MessageHandler MessageHandler}*/
    public MessageHandler(
            UserContextRepository userContextRepository,
            LoggedUsersRepository loggedUsersRepository,
            CommandHandler commandHandler,
            HandlerEditUserService updateUserServiceHandler,
            HandlerCreateOrderService handlerCreateOrderService,
            HandlerEditOrderService handlerEditOrderService,
            HandlerCancelOrderService handlerCancelOrderService,
            HandlerAcceptOrderService handlerAcceptOrderService,
            HandlerCloseOrderCourierService handlerCloseOrderService,
            HandlerCloseOrderClientService handlerCloseOrderClientService) {
        this.userContextRepository = userContextRepository;
        this.loggedUsersRepository = loggedUsersRepository;
        this.commandHandler = commandHandler;
        this.handlerEditUserService = updateUserServiceHandler;
        this.handlerCreateOrderService = handlerCreateOrderService;
        this.handlerEditOrderService = handlerEditOrderService;
        this.handlerCancelOrderService = handlerCancelOrderService;
        this.handlerAcceptOrderService = handlerAcceptOrderService;
        this.handlerCloseOrderCourierService = handlerCloseOrderService;
        this.handlerCloseOrderClientService = handlerCloseOrderClientService;
    }

    /** Первичный метод обработки сообщения. Если у пользователя, отправившего сообщение есть
     * контекст, направляет сообщение в соответствующий сервисный обработчик, если отправленное
     * сообщение является командой, перенаправляет сообщение в обработчик команд. Иначе сообщает
     * пользователю, что сообщение некорректно.
     */
    public void handle(Message msg) {
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
            return;
        }

        if(user == null) {
            if (msg.getText().equals("/start")) {
                commandHandler.handle(msg);
                return;
            }
            msg.getBotFrom().sendTextMessage(
                    msg.getUserIdOnPlatform(),
                    "отправьте /start для последующей работы."
            );
            return;
        }

        msg.setUser(user);
        try {userContext = userContextRepository.getUserContext(user.getId());}
        catch (SQLException e) {
            msg.getBotFrom().sendTextMessage(
                    msg.getUserIdOnPlatform(),
                    "Проблемы с доступом к базе данных " + e.getMessage());
            return;
        }

        msg.setUserContext(userContext);
        switch(userContext.getState()) {
            case NO_STATE -> commandHandler.handle(msg);
            case EDIT_USER -> handlerEditUserService.handle(msg);
            case ORDER_CREATING -> handlerCreateOrderService.handle(msg);
            case ORDER_EDITING -> handlerEditOrderService.handle(msg);
            case ORDER_CANCELING-> handlerCancelOrderService.handle(msg);
            case ORDER_ACCEPTING -> handlerAcceptOrderService.handle(msg);
            case ORDER_CLOSING_COURIER -> handlerCloseOrderCourierService.handle(msg);
            case ORDER_CLOSING_CLIENT -> handlerCloseOrderClientService.handle(msg);
        }
    }
}
