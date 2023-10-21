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
 * Класс, отвечающий за работу с таблицей users базы данных.
 */
public class UserRepository extends Repository {
    private static final Logger log = Logger.getLogger(UserRepository.class.getName());
    public UserRepository(DB db) {
        super(db);
    }

    /**
     * Сохраняет переданного пользователя в таблицу users базы данных.
     * @param user пользователь, которого нужно сохранить. Поля {@link User#description description} и
     * {@link User#name name} должны быть не null.
     * @return Если успешно - ссылка на переданного пользователя с присвоенным {@link User#id id}.
     * Если поля передаваемого пользователя некорректны, то {@link User#id id} выставляется в <b>-1</b>.
     * @throws SQLException
     */
    public User save(User user) throws SQLException {
        //todo сделать рефактор без try/catch
        if(user.name == null || user.description == null) {
            // todo сделать поля пользователя приватными, тогда проверка не будет нужна.
            user.id = -1;
            return user;
        }

        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String request = """
                INSERT INTO users (
                name,
                description,
                date_created
                ) VALUES ("%s", "%s", "%s");""".formatted(user.name, user.description, date);
        int response = 0;
        Statement statement = db.getConnection().createStatement();
        try {response = statement.executeUpdate(request);}
        catch (SQLException e) {
            UserRepository.log.error("Не получилось добавить %s".formatted(user), e);
            statement.close();
            throw e;
        }
        if(response == 0) {
            UserRepository.log.error("Не получилось добавить %s".formatted(user));
            statement.close();
            throw new SQLException("Something went wrong.");
        }

        ResultSet resultSet;
        request = "SELECT max(id) FROM users;";
        try {resultSet = statement.executeQuery(request);}
        catch (SQLException e) {
            UserRepository.log.error("Не удалось извлечь max id.");
            statement.close();
            throw e;
        }

        resultSet.next();
        user.id = resultSet.getLong(1);
        resultSet.close();
        statement.close();
        UserRepository.log.info("добавлен новый пользователь %s".formatted(user));
        return user;
    }

    /**
     * Возвращает пользователя с указанным идентификатором из базы данных.
     * @param id идентификатор пользователя
     * @return {@link User}, если пользователь с переданным идентификатором существует. иначе <b>null</b>
     * @throws SQLException
     */
    public User getById(long id) throws SQLException {
        if (id <= 0)
            return null;
        String request = "SELECT name, description FROM users WHERE id = %d;".formatted(id);
        Statement statement = db.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(request);
        if (resultSet.next()) {
            User res = new User(
                    id,
                    resultSet.getString("name"),
                    resultSet.getString("description")
            );
            statement.close();
            return res;
        }
        statement.close();
        return null;
    }

    /**
     * Удаляет пользователя с переданным идентификатором
     * @param id идентификатор пользователя (натуральное число)
     * @return <b>1</b>, если пользователь успешно удален, иначе <b>0</b>.
     * @throws SQLException
     */
    public int deleteUserWithId(long id) throws SQLException {
        if(id <= 0)
            return 0;
        String request = "DELETE FROM users WHERE id = %d".formatted(id);
        Statement statement = db.getConnection().createStatement();
        int response = statement.executeUpdate(request);
        if(response == 0) {return 0;}
        UserRepository.log.info("пользователь с id = %d удален.".formatted(id));
        return 1;
    }

    /**
     * Обновляет пользователя в базе данных.
     * @param user необходимо, чтобы запись с {@link User#id id} передаваемого пользователя существовала в таблице.
     * @return 1, если пользователь успешно обновлен. Иначе 0
     * @throws SQLException
     */
    public int updateUser(User user) throws SQLException {
        String request = """
                UPDATE users SET 
                name = "%s",
                description = "%s"
                WHERE id = %d;
                """.formatted(user.name, user.description, user.id);
        Statement statement = db.getConnection().createStatement();
        int response = statement.executeUpdate(request);
        if(response == 0) {return 0;}
        UserRepository.log.info("Пользователь %s обновлен.".formatted(user));
        return 1;
    }

    /**
     * @return Массив всех существующих пользователей, хранящихся в базе данных
     * @throws SQLException
     */
    public ArrayList<User> getAll() throws SQLException {
        ArrayList<User> ret = new ArrayList<>();
        String request = "SELECT id, name, description FROM users;";
        Statement statement = db.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(request);
        while(resultSet.next()) {
            ret.add(
                    new User(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("description")
                    )
            );
        }
        resultSet.close();
        statement.close();
        return ret;
    }
}
