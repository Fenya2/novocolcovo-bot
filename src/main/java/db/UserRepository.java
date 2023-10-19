package db;

import entities.User;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Класс, отвечающий за извлечение и внесение пользователей в Базу данных.
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
     * В других непредвиденных случаях {@link User#id id} выставляется в <b>-42</b>.
     * @throws SQLException <b>неплохо было бы избавиться от этой аннотации.</b>
     */
    public User save(User user) throws SQLException {
        if(user.name == null || user.description == null) {
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
            System.err.printf("Не получилось добавить %s%n", user);
            log.warn("Не получилось добавить %s".formatted(user), e);
            statement.close(); // todo нужно ли закрывать? Хочется избежать выбрасывания исключения SQLException
            user.id = -42;
            return user;
        }
        if(response == 0) {
            System.err.printf("Не получилось добавить %s%n", user);
            log.warn("Не получилось добавить %s".formatted(user));
            statement.close();
            user.id = -42;
            return user;
        }

        ResultSet resultSet;
        request = "SELECT max(id) FROM users;";
        try {resultSet = statement.executeQuery(request);}
        catch (SQLException e) {
            System.err.println("Не получилось извлечь max id");
            log.warn("Не получилось извлечь max id%s");
            statement.close();
            user.id = -42;
            return user;
        }

        resultSet.next();
        user.id = resultSet.getInt(1);
        log.info("добавлен новый пользователь %s".formatted(user));
        statement.close();
        return user;
    }
}
