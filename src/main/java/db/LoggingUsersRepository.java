package db;

import models.Domain;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Класс для работы с таблицей loggind_users базы данных.
 */
public class LoggingUsersRepository extends Repository {
    private static final Logger log = Logger.getLogger(LoggingUsersRepository.class.getName());

    public LoggingUsersRepository(DB db) {
        super(db);
    }

    /**
     * Сохраняет переданный домен в таблицу.
     * @param domain
     */
    public void saveDomain(Domain domain) throws DBException {
        String request = """
                INSERT INTO logging_users(
                login_context,
                required_login,
                from_platform,
                id_on_platform,
                verification_platform,
                verification_code
                ) VALUES (
                %d,
                "%s",
                "%s",
                "%s",
                "%s",
                %d
                );""".formatted(
                        domain.getLoginContext(),
                domain.getRequiredLogin(),
                domain.getFromPlatform(),
                domain.getIdOnPlatform(),
                domain.getVerificationPlatform(),
                domain.getVerificationCode()
                );
        try {
            Statement statement = super.db.getStatement();
            statement.executeUpdate(request);

            log.info("Добавлен домен %s".formatted(domain));
        } catch (SQLException e) {
            log.error("Не удалось добавить домен %s \n %s".formatted(domain, e.getMessage()));
            throw new DBException(e.getMessage());
        }
    }
}
