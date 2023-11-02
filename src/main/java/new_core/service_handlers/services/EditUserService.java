package new_core.service_handlers.services;

import config.services.UpdateUserServiceConfig;
import db.UserContextRepository;
import db.UserRepository;
import models.User;
import models.UserContext;
import models.UserState;

import java.sql.SQLException;

public class EditUserService extends Service{

    /** таблица пользователей */
    private final UserRepository ur;

    /** Конструктор Сервиса */
    public EditUserService(UserContextRepository ucr, UserRepository ur) {
        super(ucr);
        this.ur = ur;
    }

    /**
     * Обновляет в базе данных имя пользователя на указанное.
     * @param username новое имя пользователя
     * @param user пользователь
     */
    public void updateUsername(String username, User user) throws SQLException {
        user.setName(username);
        ur.updateUser(user);
    }

    /**
     * Обновляет в базе данных описание пользователя на указанное.
     * @param description новаое описание пользвоателя
     * @param user пользователь
     * @throws SQLException
     */
    public void updateDescription(String description, User user) throws SQLException {
        user.setDescription(description);
        ur.updateUser(user);
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
     * Начинает процедуру изменения описания пользователя в системе, меняет контекст пользователя на
     * {@link UserState EDIT_USER}
     * @param userId идентификатор пользователя, с которым нужно начать сессию.
     * @return приветственное сообщение сервиса. Содержащее команды, с помощью которых можно
     * изменить описание пользователя, его имя.
     */
    @Override
    public String startSession(long userId) {
        UserContext userContext = new UserContext(UserState.EDIT_USER);
        try {
            super.userContextRepository.updateUserContext(userId, userContext);
        } catch (SQLException e) {
            return "Ошибка при обращении к базе данных." + e.getMessage();
        }
        return UpdateUserServiceConfig.START_MESSAGE.getStr();
    }

    /**
     * Завершает сессию с пользователем, удаяя его контекст и возвращая сообщение с прощанием.
     * @param userId идентификатор пользователя.
     * @return сообщение о заверщении сессии пользователя с этим сервисом.
     */
    @Override
    public String endSession(long userId) {
        try {
            super.userContextRepository.deleteUserContext(userId);
        } catch (SQLException e) {
            return "Ошибка при удалении контекста пользователя с id " + userId + e.getMessage();
        }
        return UpdateUserServiceConfig.END_MESSAGE.getStr();
    }

    @Override
    public String getHelpMessage() {
        return UpdateUserServiceConfig.START_MESSAGE.getStr();
    }
}
