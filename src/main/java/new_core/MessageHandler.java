package new_core;

import db.LoggedUsersRepository;
import db.UserContextRepository;
import models.Message;
import models.User;
import models.UserContext;
import new_core.service_handlers.handlers.HandlerCancelOrderService;
import new_core.service_handlers.handlers.HandlerEditOrderService;
import new_core.service_handlers.handlers.HandlerCreateOrderService;
import new_core.service_handlers.handlers.HandlerEditUserService;

import java.sql.SQLException;

/**
 * Класс, обрабатывающий сообщения, поступаюшие от пользователей из разных платформ, направляющий
 * сообщение в нужный сервисный обработчик, в зависимости от контекста пользователя для последующей
 * обработки
 */
public class MessageHandler {
    /** Таблица контекстов пользователей. Должен только читать таблицу! */
    private final UserContextRepository userContextRepository;
    /** Таблица залогинившихся пользователелй. */
    private final LoggedUsersRepository loggedUsersRepository;
    private final CommandHandler commandHandler;
    private final HandlerEditUserService handlerEditUserService;
    private final HandlerCreateOrderService handlerCreateOrderService;
    private final HandlerEditOrderService handlerEditOrderService;
    private final HandlerCancelOrderService handlerCancelOrderService;


    /**
     * @param userContextRepository     таблица контекстов пользователей
     * @param handlerCreateOrderService
     * @param handlerEditOrderService
     * @param handlerCancelOrderService
     */
    public MessageHandler(
            UserContextRepository userContextRepository,
            LoggedUsersRepository loggedUsersRepository,
            CommandHandler commandHandler,
            HandlerEditUserService updateUserServiceHandler,
            HandlerCreateOrderService handlerCreateOrderService, HandlerEditOrderService handlerEditOrderService, HandlerCancelOrderService handlerCancelOrderService) {
        this.userContextRepository = userContextRepository;
        this.loggedUsersRepository = loggedUsersRepository;
        this.commandHandler = commandHandler;
        this.handlerEditUserService = updateUserServiceHandler;
        this.handlerCreateOrderService = handlerCreateOrderService;
        this.handlerEditOrderService = handlerEditOrderService;
        this.handlerCancelOrderService = handlerCancelOrderService;
    }

    /** первичный метод обработки сообщения. Если у пользователя, отправившего сообщение есть
     * контекст, напрявляет сообщение в соответствующий сервисный обработчик, если отправленное
     * сообщение является командой, перенаправляет сообщение в обработчик команд. Иначе сообщает
     * пользователю, что сообщение некорректно.
     */
    public void handle(Message msg) {
        User user = null;
        UserContext userContext = null;
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
                    "отправтьте /start для последующей работы."
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
        }
    }
}
