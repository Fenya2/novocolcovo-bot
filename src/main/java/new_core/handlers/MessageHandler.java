package new_core.handlers;

import db.LoggedUsersRepository;
import db.UserContextRepository;
import models.Message;
import models.User;
import models.UserContext;
import new_core.handlers.service_handlers.UpdateUserServiceHandler;

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
    private final UpdateUserServiceHandler updateUserServiceHandler;

    /** @param userContextRepository таблица контекстов пользователей */
    public MessageHandler(
            UserContextRepository userContextRepository,
            LoggedUsersRepository loggedUsersRepository,
            CommandHandler commandHandler,
            UpdateUserServiceHandler updateUserServiceHandler
    ) {
        this.userContextRepository = userContextRepository;
        this.loggedUsersRepository = loggedUsersRepository;
        this.commandHandler = commandHandler;
        this.updateUserServiceHandler = updateUserServiceHandler;
    }

    /** первичный метод обработки сообщения. Если у пользователя, отправившего сообщение есть
     * контекст, напрявляет сообщение в соответствующий сервисный обработчик, если отправленное
     * сообщение является командой, перенаправляет сообщение в обработчик команд. Иначе сообщает
     * пользователю, что сообщение некорректно.
     */
    public void handle(Message message) {
        User user = null;
        UserContext userContext = null;
        try {
            user = loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                    message.getPlatform(),
                    message.getUserIdOnPlatform()
            );
        } catch (SQLException e) {
            message.getBotFrom().sendTextMessage(
                    message.getUserIdOnPlatform(),
                    "Проблемы с доступом к базе данных " + e.getMessage()
            );
            return;
        }
        if(user == null) {
            message.getBotFrom().sendTextMessage(
                    message.getUserIdOnPlatform(),
                    "отправтьте /start для последующей работы."
            );
            return;
        }
        message.setUser(user);
        try {userContext = userContextRepository.getUserContext(user.getId());}
        catch (SQLException e) {
            message.getBotFrom().sendTextMessage(
                    message.getUserIdOnPlatform(),
                    "Проблемы с доступом к базе данных " + e.getMessage());
            return;
        }

        if(userContext == null) {
            switch (message.getPlatform()) {
                case TELEGRAM -> {
                    if(message.getText().charAt(0) == '/') {
                        commandHandler.handle(message);
                        return;
                    }
                }
            }
        }

        message.setUserContext(userContext);
        switch(userContext.getState()) {
            case EDIT_USER -> updateUserServiceHandler.handle(message);
        }
    }
}
