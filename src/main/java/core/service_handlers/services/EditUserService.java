package core.service_handlers.services;

import config.services.EditUserServiceConfig;
import db.DBException;
import db.UserContextRepository;
import db.UserRepository;
import models.User;
import models.UserContext;
import models.UserState;

import java.sql.SQLException;

/**
 * Класс для обновления пользователя. Подразумевается, что данные, которые приходят в этот сервис
 * для обработки, являются правильными, поэтому здесь входные данные на корректность не проверяются.
 */
public class EditUserService extends Service {
    private UserRepository ur;
    private RateUserService rateUserService;

    public EditUserService(UserContextRepository ucr, UserRepository ur, RateUserService rateUserService) {
        super(ucr);
        this.rateUserService = rateUserService;
        this.ur = ur;
    }

    /**
     * Герерирует сообщение с информацией о пользователе.
     * @param userId id пользователя
     * @return
     */
    public String generateProfileMessage(long userId) {
        assert userId >= 0;
        User user;
        double userRate;
        try {
            user = ur.getById(userId);
            setEditUserContext(userId);
            userRate = rateUserService.getUserRate(userId);
        } catch (DBException | SQLException e) {
            return "Ошибка при обращении к базе данных." + e.getMessage();
        }
        assert user != null;
        return """
                Ваш профиль:
                Логин: %s
                Имя: %s
                Описание: %s
                Рейтинг: %s
                """.formatted(user.getLogin(), user.getName(), user.getDescription(),
                (userRate+"0").substring(0,4));
    }

    /**
     * Завершает сессию с пользователем, удаляя его контекст и возвращая сообщение с прощанием.
     *
     * @param userId идентификатор пользователя.
     * @return сообщение о завершении сессии пользователя с этим сервисом.
     */
    public String endSession(long userId) {
        try {
            unsetEditUserContext(userId);
        } catch (SQLException e) {
            return "Ошибка при удалении контекста пользователя с id " + userId + e.getMessage();
        }
        return EditUserServiceConfig.END_MESSAGE.getStr();
    }

    @Override
    public String getHelpMessage() {
        return EditUserServiceConfig.HELP_MESSAGE.getStr();
    }

    /**
     * Обновляет в базе данных имя пользователя на указанное.
     *
     * @param username новое имя пользователя.
     * @param user     пользователь.
     * @return переданный пользователь с новым именем.
     */
    public User updateUsername(String username, User user) throws DBException {
        assert username != null;
        assert user != null;
        assert user.getId() >= 0;
        user.setName(username);
        ur.updateUser(user);
        return user;
    }

    /**
     * Обновляет в базе данных описание пользователя на указанное.
     *
     * @param description новое описание пользвоателя.
     * @param user        пользователь.
     * @return переданный пользователь с новым описанием.
     */
    public User updateDescription(String description, User user) throws DBException {
        user.setDescription(description);
        ur.updateUser(user);
        return user;
    }

    /**
     * Обновляет в базе данных описание пользователя на указанное.
     *
     * @param login новый логин пользвоателя.
     * @param user        пользователь.
     * @return переданный пользователь с новым логином, если тот не занят. Иначе старый пользователь.
     */
    public boolean updateLogin(String login, User user) throws DBException {
        if(user.getLogin().equals(login)) {
            return  true;
        }
        if(!ur.isValidLogin(login)) {
            return false;
        }
        user.setLogin(login);
        ur.updateUser(user);
        return true;
    }

    /**
     * Устанавливает номер состояния пользователя в 1, что значит, что следующее сообщение
     * пользователя будет содержать новое имя.
     *
     * @return контекст, который устанавливается пользователю.
     */
    public UserContext setEditUsernameContext(long userId) throws SQLException {
        UserContext userContext = new UserContext(UserState.EDIT_USER, 1);
        userContextRepository.updateUserContext(userId, userContext);
        return userContext;
    }

    /**
     * Устанавливает номер состояния пользователя в 2, что значит, что следующее сообщение
     * пользователя будет содержать новое описание.
     */
    public UserContext setEditDescriptionContext(long userId) throws SQLException {
        UserContext userContext = new UserContext(UserState.EDIT_USER, 2);
        userContextRepository.updateUserContext(userId, userContext);
        return userContext;
    }

    /**
     * Устанавливает номер состояния пользователя в 3, что значит, что следующее сообщение
     * пользователя будет содержать новый логин.
     */
    public UserContext setEditLoginContext(long userId) throws SQLException {
        UserContext userContext = new UserContext(UserState.EDIT_USER, 3);
        userContextRepository.updateUserContext(userId, userContext);
        return userContext;
    }

    /**
     * Устанавливает номер состояния пользователя в 0 - дефолтное состояние в контексте
     * {@link UserState EDIT_USER}
     */
    public UserContext resetEditContext(long userId) throws SQLException {
        UserContext userContext = new UserContext(UserState.EDIT_USER, 0);
        userContextRepository.updateUserContext(userId, userContext);
        return userContext;
    }

    /**
     * Устанавливает контекст пользователя в {@link UserState EDIT_USER}.
     *
     * @param userId id пользователя.
     */
    private void setEditUserContext(long userId) throws SQLException, DBException {
        UserContext userContext = new UserContext(UserState.EDIT_USER);
        super.userContextRepository.saveUserContext(userId, userContext);
    }

    /**
     * Устанавливает контекст пользователя в {@link UserState NO_STATE}.
     *
     * @param userId id пользователя.
     */
    private void unsetEditUserContext(long userId) throws SQLException {
        UserContext userContext = new UserContext(UserState.NO_STATE);
        super.userContextRepository.updateUserContext(userId, userContext);
    }
}
