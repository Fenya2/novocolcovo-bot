package db;

import models.Domain;
import models.Platform;
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
     */
    public void saveDomain(Domain domain) throws DBException {
        if(domain == null) {
            throw new DBException("Некорректный параметр domain. должен быть не null.");
        }
        if(getDomainByFromPlatformAndIdOnPlatform(domain.getFromPlatform(), domain.getIdOnPlatform()) != null) {
            throw new DBException("Невозможно сохранить переданный домен, так как он уже есть в таблице");
        }
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
        try (Statement statement = db.getStatement()) {
            statement.executeUpdate(request);
            log.info("Добавлен домен %s".formatted(domain));
        } catch (SQLException e) {
            log.error("Не удалось добавить домен %s \n %s".formatted(domain, e.getMessage()));
            throw new DBException(e.getMessage());
        }
    }

    /**
     * Обновляет старый домен таблицы переданным.
     */
    public void updateDomain(Domain domain) throws DBException{
        if(domain == null) {
            throw new DBException("Некорректный параметр domain. должен быть не null.");
        }
        if(getDomainByFromPlatformAndIdOnPlatform(domain.getFromPlatform(), domain.getIdOnPlatform()) == null) {
            throw new DBException("Невозможно обновить переданный домен, так как его нет в таблице");
        }

        String request =
                """
                UPDATE logging_users
                SET
                login_context = %d,
                required_login = "%s",
                verification_platform = "%s",
                verification_code = %d
                WHERE
                verification_platform = "%s" AND
                id_on_platform = "%s";
                """.formatted(
                        domain.getLoginContext(),
                        domain.getRequiredLogin(),
                        domain.getVerificationPlatform(),
                        domain.getVerificationCode(),
                        domain.getVerificationPlatform(),
                        domain.getIdOnPlatform()
                );

        try(Statement statement = db.getStatement()) {
            statement.executeUpdate(request);
            log.info("Домен %s обновлен.".formatted(domain));
        } catch (SQLException e) {
            log.error("Не удалось обновить на домен %s.\n%s".formatted(domain, e.getMessage()));
            throw new DBException(e.getMessage());
        }
    }

    /**
     * Удаляет домен из таблицы по идентификатору.
     */
    public void deleteDomainByFromPlatformAndIdOnPlatform(Platform fromPlatform, String idOnPlatform)
        throws DBException {
        if(fromPlatform == null || idOnPlatform == null) {
            throw new DBException("Некорректные параметры. Должны быть не null");
        }
        String request =
                """
                DELETE FROM logging_users
                WHERE from_platform = "%s" AND id_on_platform = "%s"
                """.formatted(fromPlatform, idOnPlatform);
        int retCode;
        try(Statement statement = db.getStatement()) {
            retCode = statement.executeUpdate(request);
        } catch (SQLException e) {
            log.error("Не удалось удалить домен по %s и %s \n %s"
                    .formatted(fromPlatform, idOnPlatform, e.getMessage()));
            throw new DBException(e.getMessage());
        }
        if(retCode == 0)
            throw new DBException("Не удалось удалить домен по указанным параметрам, так как его не было в таблице");
    }

    /**
     * @param fromPlatform платформа, с которой пишет пользователь
     * @param idOnPlatform id пользователя на этой платформе
     * @return домен пользователя, если он есть. Иначе <b>null</b>
     */
    public Domain getDomainByFromPlatformAndIdOnPlatform(Platform fromPlatform, String idOnPlatform)
            throws DBException {
        if(fromPlatform == null || idOnPlatform == null) {
            throw new DBException("Некорректные параметры. Должны быть не null");
        }
        String query =
                """
                SELECT
                login_context,
                required_login,
                from_platform,
                id_on_platform,
                verification_platform,
                verification_code
                FROM logging_users
                WHERE from_platform = "%s" AND id_on_platform = "%s";
                """.formatted(fromPlatform, idOnPlatform);
        try(Statement statement = db.getStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            if(!resultSet.next())
                return null;
            Domain domain = new Domain()
                    .loginContext(resultSet.getInt("login_context"))
                    .requiredLogin(resultSet.getString("required_login"))
                    .fromPlatform(Platform.fromString(resultSet.getString("from_platform")))
                    .idOnPlatform(resultSet.getString("id_on_platform"))
                    .verificationPlatform(Platform.fromString(resultSet.getString("verification_platform")))
                    .verificationCode(resultSet.getInt("verification_code"));
            return domain;
        } catch (SQLException e) {
            log.error("Не удалось получить домен по %s и %s \n %s"
                    .formatted(fromPlatform, idOnPlatform, e.getMessage()));
            throw new DBException(e.getMessage());
        }
    }
}
