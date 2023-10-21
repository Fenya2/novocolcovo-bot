package db;

import models.User;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Класс, отвечающий за работу с таблицей LoggedUsers базы данных.
 */
public class LoggedUsersRepository extends Repository {
    private static final Logger log = Logger.getLogger(LoggedUsersRepository.class.getName());
    private final UserRepository userRepository;

    public LoggedUsersRepository(DB db, UserRepository userRepository) {
        super(db);
        this.userRepository = userRepository;
    }

    /**
     * Связывает идентификатор пользователя в программе и платформу, с которой пользователь
     * взаимодействует с программой, используя его идентификатор на этой платформе.
     *
     * @param userId       идентификатор пользователя в программе. Пользователь с таким идентификатором
     *                     должен быть в таблице users.
     * @param platform     платформа, с которой пользователь взаимодействует с программой. Не <b>null</b>.
     * @param idOnPLatform идентификатор пользователя на указанной платформе. Не <b>null</b>.
     * @return <b>1</b>, если добавление связи прошло успешно,
     * <b>-1</b>, если
     * platform <b>null</b>,
     * или идентификатор пользователя на платформе <b>null</b>,
     * или пользователя с указанным id нет в таблице users,
     * @throws SQLException
     */
    public int linkUserIdAndUserPlatform(long userId, String platform, String idOnPLatform) throws SQLException {
        if (userId <= 0
                || platform == null
                || idOnPLatform == null
                || userRepository.getUserById(userId) == null) {
            return -1;
        }
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String request = """
                INSERT INTO logged_users (
                user_id,
                platform,
                id_on_platform,
                login_date
                ) VALUES (%d, "%s", "%s", "%s");
                """.formatted(userId, platform, idOnPLatform, date);
        Statement statement = db.getConnection().createStatement();
        if (statement.executeUpdate(request) != 1) {
            statement.close();
            throw new SQLException("something went wrong.");
        }
        statement.close();
        LoggedUsersRepository.log.info("Пользователь с id = %d зашел с платформы %s."
                .formatted(userId, platform));
        return 1;
    }

    /**
     * По идентификатору пользователя на платформе, с которой пользователь взаимодействует
     * с программой, определяет пользователя в программе.
     *
     * @param platform     платформа, с которой пользователь взаимодействует с программой. Не <b>null</b>.
     * @param idOnPlatform идентификатор пользователя на указанной платформе. Не <b>null</b>.
     * @return Если успешно (связь существует) - объект класса {@link User} с заполненными полями.
     * Иначе <b>null</b>.
     * @throws SQLException
     */
    public User getUserByPlatformAndIdOnPlatform(String platform, String idOnPlatform) throws SQLException {
        if (platform == null || idOnPlatform == null) return null;
        //todo или делать один sql запрос, cразу возвращая нужную строку таблицы users, если она найдется?
        String request = """
                SELECT user_id FROM logged_users
                WHERE platform = "%s"
                AND id_on_platform = "%s";
                """.formatted(platform, idOnPlatform);
        Statement statement = db.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(request);
        if (!resultSet.next()) {
            resultSet.close();
            statement.close();
            return null;
        }
        long userId = resultSet.getLong("user_id");
        resultSet.close();
        statement.close();
        return userRepository.getUserById(userId);
    }

    /**
     * Разрушает связь между пользователем в программе и платформой, через которую пользователь
     * взаимодействовал с программой.
     *
     * @param userId   идентификатор пользователя в программе.
     * @param platform платформа, с которой пользователь взаимодействует с программой. Не <b>null</b>.
     * @return <b>1</b>, если успешно.
     * <b>-1</b>, если <i>platform</i> null.
     * <b>-2</b>, если связи не существует и удалять нечего.
     * @throws SQLException
     */
    public int deleteLink(long userId, String platform) throws SQLException {
        if (platform == null) {
            return -1;
        }
        if (userId <= 0) {
            return -2;
        }
        String request = "DELETE FROM logged_users WHERE user_id = %d AND platform = \"%s\""
                .formatted(userId, platform);
        Statement statement = db.getConnection().createStatement();
        if (statement.executeUpdate(request) == 0) {
            statement.close();
            return -2;
        }
        LoggedUsersRepository.log.info("Связь пользователя с id = %d с платформой %s разрушена"
                .formatted(userId, platform));
        statement.close();
        return 1;
    }
}
