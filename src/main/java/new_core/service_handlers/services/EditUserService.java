package new_core.service_handlers.services;

import config.services.EditUserServiceConfig;
import db.UserContextRepository;
import db.UserRepository;
import models.User;
import models.UserContext;
import models.UserState;

import java.sql.SQLException;

/** Сервис для работы с контекстом {@link models.UserState#EDIT_USER EDIT_USER}*/

public class EditUserService {

    /** @see UserContextRepository*/
    private final UserContextRepository userContextRepository;

    /** @see UserRepository */
    private final UserRepository userRepository;

    /** Конструктор {@link EditUserService}*/
    public EditUserService(UserContextRepository userContextRepository, UserRepository userRepository) {
        this.userContextRepository = userContextRepository;
        this.userRepository = userRepository;
    }

    /**
     * Обновляет в базе данных имя пользователя на указанное.
     * @param username новое имя пользователя
     * @param user пользователь
     */
    public void updateUsername(String username, User user) throws SQLException {
        user.setName(username);
        userRepository.updateUser(user);
    }

    /**
     * Обновляет в базе данных описание пользователя на указанное.
     * @param description новое описание пользователя
     * @param user пользователь
     */
    public void updateDescription(String description, User user) throws SQLException {
        user.setDescription(description);
        userRepository.updateUser(user);
    }

    /**
     * Устанавливает номер состояния пользователя в 1, что значит, что следующее сообщение
     * пользователя будет содержать новое имя.
     */
    public void setEditUsernameContext(long userId) throws SQLException {
        UserContext userContext = new UserContext(UserState.EDIT_USER, 1);
        userContextRepository.updateUserContext(userId, userContext);
    }

    /**
     * Устанавливает номер состояния пользователя в 2, что значит, что следующее сообщение
     * пользователя будет содержать новое описание.
     */
    public void setEditDescriptionContext(long userId) throws SQLException {
        UserContext userContext = new UserContext(UserState.EDIT_USER, 2);
        userContextRepository.updateUserContext(userId, userContext);
    }

    /**
     * Устанавливает номер состояния пользователя в 0 - дефолтное состояние в контексте
     * {@link UserState EDIT_USER}
     */
    public void resetEditContext(long userId) throws  SQLException {
        UserContext userContext = new UserContext(UserState.EDIT_USER, 0);
        userContextRepository.updateUserContext(userId, userContext);
    }

    /**
     * Завершает сессию с пользователем, удаляя его контекст и возвращая сообщение с прощанием.
     * @param userId идентификатор пользователя.
     * @return сообщение о завершении сессии пользователя с этим сервисом.
     */
    public String endSession(long userId) {
        try {
            userContextRepository.updateUserContext(userId,new UserContext());
        } catch (SQLException e) {
            return "Ошибка при удалении контекста пользователя с id " + userId + e.getMessage();
        }
        return EditUserServiceConfig.END_MESSAGE.getStr();
    }

}
