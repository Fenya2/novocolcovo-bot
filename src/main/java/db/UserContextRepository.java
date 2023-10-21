package db;

import models.UserContext;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserContextRepository extends Repository{

    private static final Logger log = Logger.getLogger(UserContextRepository.class.getName());
    private UserRepository userRepository;
    public UserContextRepository(DB db, UserRepository userRepository) {
        super(db);
        this.userRepository = userRepository;
    }


    /**
     * Создает контекст при взаимодействии с пользователем.
     * @param userId идентификатор пользователя, существующего в таблице users.
     * @param userContext Контекст.
     * @return <b>1</b>, если успешно.
     * <b>-1</b>, если контекст при взаимодействии с пользователем уже существует.
     * <b>-2</b>, если пользователя с переданным идентификатором не существует.
     * @throws SQLException
     */
    public int createUserContext(long userId, UserContext userContext) throws SQLException {
        if(getUserContext(userId) != null) {
            return -1;
        }
        if(userRepository.getUserById(userId) == null) {
            return -2;
        }
        String request = """
                INSERT INTO user_contexts (
                user_id,
                state,
                state_num
                ) VALUES (%d, "%s", "%d");
                """.formatted(userId, userContext.getState(), userContext.getState_num());
        Statement statement = db.getConnection().createStatement();
        if(statement.executeUpdate(request) == 0) {
            statement.close();
            throw new SQLException("Can't add record to database");
        }
        UserContextRepository.log.info("Пользователь с id = %d начал %s"
                .formatted(userId, userContext.getState()));
        statement.close();
        return 1;
    }

    /**
     * Проверяет наличие контекста при взаимодействии с пользователем.
     * @param userId идентификатор пользователя. Пользователь с таким идентификатором должен быть
     *               в таблице users базы данных.
     * @return {@link UserContext контекст} пользователя с переданным идентификатором.
     * Если контекста нет, то <b>null</b>
     * @throws SQLException
     */
    public UserContext getUserContext(long userId) throws SQLException {
        if(userId <= 0 || userRepository.getUserById(userId) == null) {
            return null;
        }

        String request = "SELECT state, state_num FROM user_contexts WHERE user_id = %d;"
                .formatted(userId);
        Statement statement = db.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(request);
        if(!resultSet.next()) {
            return null;
        }
        UserContext userContext = new UserContext(
                resultSet.getString("state"),
                resultSet.getInt("state_num")
                );
        resultSet.close();
        statement.close();
        return userContext;
    }

    /**
     * Обновляет контекст пользователя с переданным идентификатором.
     * @param userId идентификатор пользователя
     * @return <b>1</b>, если контекст пользователя успешно обновлен.
     * Иначе <b>-1</b>
     * @throws SQLException
     */
    public int updateUserContext(long userId, UserContext userContext) throws SQLException{
        if(userId <= 0) {
            return -1;
        }
        String request = """
                UPDATE user_contexts
                SET state = "%s", state_num = %d
                WHERE user_id = %d;
                """.formatted(userContext.getState(), userContext.getState_num(), userId);
        Statement statement = db.getConnection().createStatement();
        if(statement.executeUpdate(request) == 0) {
            statement.close();
            return -1;
        }
        UserContextRepository.log.info("Пользователь с id = %d изменил свой контекст на %s"
                .formatted(userId, userContext));
        statement.close();
        return 1;
    }

    /**
     * Удаляет из таблицы user_contexts базы данных контекст пользователя
     * с переданным идентификатором
     * @param userId идентификатор пользователя.
     * @return <b>1</b>, если контекст был удален. Иначе <b>-1</b>.
     * @throws SQLException
     */
    public int deleteUserContext(long userId) throws SQLException {
        if(userId <= 0) {
            return -1;
        }
        String request = "DELETE FROM user_contexts WHERE user_id = %d".formatted(userId);
        Statement statement = db.getConnection().createStatement();
        if(statement.executeUpdate(request) == 0) {
            return -1;
        }
        UserContextRepository.log.info("Пользователь с id = %d вне контекста".formatted(userId));
        return 1;
    }
}