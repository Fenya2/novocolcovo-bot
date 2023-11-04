package db;


import models.UserContext;
import models.UserState;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Класс, отвечающий за работу с таблицей user_contexts базы данных.
 */
public class UserContextRepository extends Repository{
    private static final Logger log = Logger.getLogger(UserContextRepository.class.getName());
    private final UserRepository userRepository;
    public UserContextRepository(DB db, UserRepository userRepository) {
        super(db);
        this.userRepository = userRepository;
    }

    /**
     * Создает контекст при взаимодействии с пользователем.
     * @param userId идентификатор пользователя, существующего в таблице users.
     * @param userContext Контекст пользователя
     * @return <b>1</b>, если успешно.
     * <b>-1</b>, если контекст при взаимодействии с пользователем уже существует.
     * <b>-2</b>, если пользователя с переданным идентификатором не существует.
     */
    public int saveUserContext(long userId, UserContext userContext) throws SQLException {
        if(userRepository.getById(userId) == null) {
            return -2;
        }
        if(getUserContext(userId) != null) {
            return -1;
        }
        String request = """
                INSERT INTO user_contexts(
                user_id,
                state,
                state_num
                ) VALUES (%d, "%s", "%d");
                """.formatted(userId, userContext.getState(), userContext.getStateNum());
        Statement statement = db.getStatement();
        if(statement.executeUpdate(request) == 0) {
            statement.close();
            throw new SQLException("Something went wrong. Хотя не должно.");
        }
        UserContextRepository.log.info("Пользователь с id = %d сейчас %s"
                .formatted(userId, userContext.getState()));
        statement.close();
        return 1;
    }

    /**
     * Возвращает контекст пользователя с переданным идентификатором.
     * @param userId идентификатор пользователя.
     * @return {@link UserContext контекст} пользователя с переданным идентификатором.
     */
    public UserContext getUserContext(long userId) throws SQLException {
        if(userId <= 0) {
            throw new SQLException("No user with id %d".formatted(userId));
        }

        String request = "SELECT state, state_num FROM user_contexts WHERE user_id = %d;"
                .formatted(userId);
        Statement statement = db.getStatement();
        ResultSet resultSet = statement.executeQuery(request);
        if(!resultSet.next()) {
            throw new SQLException("No user with id %d".formatted(userId));
        }
        UserContext userContext = new UserContext(
                UserState.valueOf(resultSet.getString("state")),
                resultSet.getInt("state_num")
                );
        resultSet.close();
        statement.close();
        return userContext;
    }

    /**
     * Обновляет контекст пользователя с переданным идентификатором, если он существует.
     * @param userId идентификатор пользователя
     * @return <b>1</b>, если контекст пользователя успешно обновлен.
     * <b>-1</b> Если контекст у пользователя с переданным идентификаторон не существует.
     * @throws SQLException
     */
    public int updateUserContext(long userId, UserContext userContext) throws SQLException {
        if(userId <= 0) {
            return -1;
        }
        String request = """
                UPDATE user_contexts
                SET state = "%s", state_num = %d
                WHERE user_id = %d;
                """.formatted(userContext.getState(), userContext.getStateNum(), userId);
        Statement statement = db.getStatement();
        if(statement.executeUpdate(request) == 0) {
            statement.close();
            throw new SQLException("Something went wrong. Хотя не должно.");
        }
        UserContextRepository.log.info("Пользователь с id = %d изменил свой контекст на %s."
                .formatted(userId, userContext));
        statement.close();
        return 1;
    }

    /**
     * Удаляет из таблицы user_contexts базы данных контекст пользователя
     * с переданным идентификатором.
     * @param userId идентификатор пользователя.
     * @return <b>1</b>, если контекст был удален. Иначе <b>-1</b>.
     * @throws SQLException
     */
    public int deleteUserContext(long userId) throws SQLException {
        if(userId <= 0) {
            return -1;
        }
        String request = "DELETE FROM user_contexts WHERE user_id = %d".formatted(userId);
        Statement statement = db.getStatement();
        if(statement.executeUpdate(request) == 0) {
            statement.close();
            throw new SQLException("Something went wrong. Хотя не должно.");
        }
        UserContextRepository.log.info("Пользователь с id = %d вне контекста".formatted(userId));
        return 1;
    }
}
