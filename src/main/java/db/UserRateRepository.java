package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.sqlite.util.LibraryLoaderUtil;

public class UserRateRepository extends Repository{
    /** логер */
    private static final Logger log = Logger.getLogger(UserRateRepository.class.getName());

    /** Репозиторий пользователей */
    public UserRateRepository(DB db) {
        super(db);
    }
    
    /**
     * Добавляет пользователя с переданным id в таблицу рейтингов пользователей
     * поля суммы и количества заказов заполняются нулями.
     */
    public void save(long userId) throws DBException {
        if(userId <= 0 )
            throw new DBException("идентификатор пользователя должен быть положительным");
        
        if(haveUser(userId))
            return;
        
        String query = """
                INSERT INTO users_rate(
                user_id,
                rate_sum,
                num_of_orders
                ) VALUES (%d, %d, %d);
                """.formatted(userId, 0, 0);
        try (Statement statement = db.getStatement()) {
            statement.executeUpdate(query);
            log.info("Добавлен пользователь %s в таблицу рейтинга.".formatted(query));
        } catch (SQLException e) {
            log.error("Не удалость пользователя %d в таблицу рейтинга.\n %s"
                .formatted(userId, e.getMessage()));
            throw new DBException(e.getMessage());
        }
    }


    /**
     * Проверяет, есть ли пользователь с переданным id в таблице.
     * @param userId id пользователя.
     */
    public boolean haveUser(long userId) throws DBException{
        String query = "SELECT user_id FROM users_rate WHERE user_id=%d;"
            .formatted(userId);
        try(Statement statement = db.getStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next())
                return true;
            else return false;
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    /**
     * Обновляет рейтинг пользователя.
     */
    public int updateRate(long userId, long rateSum, long numOfOrders) throws DBException {
        if(!haveUser(userId))
            throw new DBException("пользователя %d нет в таблице");
        String query = "UPDATE users_rate SET rate_sum=%d, num_of_orders=%d WHERE user_id=%d".formatted(rateSum, numOfOrders, userId);
        try(Statement statement = db.getStatement()) {
            statement.executeUpdate(query);
            log.info("Таблица рейтинга, пользователь %d обновлен.".formatted(userId));
        } catch (SQLException e) {
            log.error("Не удалось обновить таблицу рейтинга, пользователь %d.\n%s".formatted(userId, e.getMessage()));
            throw new DBException(e.getMessage());
        }
        return 0;
    }

    /**
     * Возвращает пару рейтинговая сумма и количество заказов у пользователя.
     * @param userId идентификатор пользователя
     */
    public List<Long> getRateSumAndNumOfOrders(long userId) throws DBException {
        if(!haveUser(userId)) {
            throw new DBException("Запрашиваемого пользователя %d нет в таблице рейтинга".formatted(userId));
        }
        String query = "SELECT rate_sum, num_of_orders FROM users_rate WHERE user_id=%d;"
            .formatted(userId);
        try(Statement statement = db.getStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            List<Long> ret = new ArrayList<>();
            ret.add(resultSet.getLong("rate_sum"));
            ret.add(resultSet.getLong("num_of_orders"));
            return ret;
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }


    /**
     * Удаляет пользователя из таблица рейтинга
     */
    public void delete(long userId) throws DBException {
        if(!haveUser(userId)) {
            throw new DBException("Запрашиваемого пользователя %d нет в таблице рейтинга".formatted(userId));
        }
        String query = "DELETE FROM users_rate WHERE user_id=%d".formatted(userId);
        try(Statement statement = db.getStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new DBException("Не удалось удалить пользователя %d из таблицы рейтинга\n%s".formatted(userId, e.getMessage()));
        }   
    }
}
