package db;

import models.User;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Класс, отвечающий за работу с таблицей users базы данных. <br>
 * Cодержит информацию о пользователях.
 */
public class UserRepository extends Repository {
    private static final Logger log = Logger.getLogger(UserRepository.class.getName());
    public UserRepository(DB db) {
        super(db);
    }

    /**
     * Сохраняет переданного пользователя в таблицу users базы данных.
     * @param user пользователь, которого нужно сохранить.
     * @return Если успешно - ссылка на переданного пользователя с присвоенным {@link User#getId()} id}.
     */
    public User save(User user) throws DBException {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String request =
                """
                    INSERT INTO users (
                    name,
                    description,
                    login,
                    date_created
                    ) VALUES ("%s", "%s", "%s", "%s");
                """.formatted
                (user.getName(), user.getDescription(), user.getLogin(), date);
        try {
            Statement statement = db.getStatement();
            statement.executeUpdate(request);
            request = "SELECT max(id) FROM users;";
            ResultSet resultSet = statement.executeQuery(request);
            user.setId(resultSet.getLong(1));
            resultSet.close();
            statement.close();
            UserRepository.log.info("добавлен новый пользователь %s".formatted(user));
            return user;
        } catch (SQLException e) {
            UserRepository.log.error(e.toString());
            throw new DBException("something went wrong in DB: " + e.getMessage());
        }
    }

    /**
     * Проверяет уникальность логина для пользователя, проверяя его уникальность в система
     * @param login проверяемый логин.
     * @return true, если логин не занят. Иначе false.
     */
    public boolean isValidLogin(String login) throws DBException {
        String request ="SELECT login FROM users WHERE login = \"%s\"".formatted(login);
        try {
            ResultSet resultSet = db.getStatement().executeQuery(request);
            return !resultSet.next();
        } catch (SQLException e) {
            UserRepository.log.error(e.toString());
            throw new DBException("something went wrong in DB: " + e.getMessage());
        }
    }

    /**
     * Возвращает пользователя с указанным идентификатором из базы данных.
     * @param id идентификатор пользователя.
     * @return {@link User}, если пользователь с переданным идентификатором существует. иначе <b>null</b>
     * @throws SQLException
     */
    public User getById(long id) throws DBException {
        if (id <= 0)
            return null;
        String request = "SELECT name, description, login FROM users WHERE id = %d;".formatted(id);
        try {
            Statement statement = db.getStatement();
            ResultSet resultSet = statement.executeQuery(request);
            if (!resultSet.next()) {
                resultSet.close();
                statement.close();
                return null;
            }
            User res = new User(
                    id,
                    resultSet.getString("name"),
                    resultSet.getString("description"),
                    resultSet.getString("login")
            );
            resultSet.close();
            statement.close();
            return res;
        } catch (SQLException e) {
            UserRepository.log.error(e.toString());
            throw new DBException("something went wrong in DB: " + e.getMessage());
        }
    }

    /**
     * Удаляет пользователя с переданным идентификатором
     * @param id идентификатор пользователя.
     * @return <b>1</b>, если пользователь успешно удален, иначе <b>0</b>.
     * @throws SQLException
     */
    public int deleteUserWithId(long id) throws SQLException {
        if(id <= 0)
            return 0;
        String request = "DELETE FROM users WHERE id = %d".formatted(id);
        Statement statement = db.getStatement();
        if(statement.executeUpdate(request) == 0) {
            statement.close();
            return 0;
        }
        statement.close();
        UserRepository.log.info("пользователь с id = %d удален.".formatted(id));
        return 1;
    }

    /**
     * Обновляет пользователя в базе данных.
     * @param user необходимо, чтобы запись с {@link User#getId() id} передаваемого пользователя существовала в таблице.
     * @return 1, если пользователь успешно обновлен. Иначе 0
     * @throws SQLException
     */
    public int updateUser(User user) throws DBException {
        if(user.getId() <= 0) {
            return 0;
        }
        String request = """
                UPDATE users SET
                name = "%s",
                description = "%s",
                login = "%s"
                WHERE id = %d;
                """.formatted(user.getName(), user.getDescription(), user.getLogin(), user.getId());
        try {
            Statement statement = db.getStatement();
            if(statement.executeUpdate(request) == 0) {
                statement.close();
                return 0;
            }
            statement.close();
            UserRepository.log.info("Пользователь %s обновлен.".formatted(user));
            return 1;
        } catch (SQLException e) {
            UserRepository.log.error(e.toString());
            throw new DBException("something went wrong in DB: " + e.getMessage());
        }
    }

    /**
     * @return Массив всех существующих пользователей, хранящихся в базе данных
     * @throws SQLException
     */
    public ArrayList<User> getAll() throws SQLException {
        ArrayList<User> ret = new ArrayList<>();
        String request = "SELECT id, name, description, login FROM users;";
        Statement statement = db.getStatement();
        ResultSet resultSet = statement.executeQuery(request);
        while(resultSet.next()) {
            ret.add(
                    new User(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("description"),
                            resultSet.getString("login")
                    )
            );
        }
        resultSet.close();
        statement.close();
        return ret;
    }
}
