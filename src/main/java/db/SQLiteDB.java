
package db;

import config.SQLDBconfig;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Реазизация интерфейса
 */
public class SQLiteDB implements DB{
    private static final Logger log = Logger.getLogger(SQLiteDB.class.getName());

    private SQLDBconfig config;

    /** Объект подключения к бд. */
    private Connection conn;

    /** @param config путь к файлу конфигурации бд. */
    public SQLiteDB(SQLDBconfig config) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        this.config = config;
    }


    /** Сущность, передаваемая репозиториям для выполнения запросов к БД. */
    @Override
    public Statement getStatement() throws SQLException {
        return conn.createStatement();
    }

    /**
     * Подключается к базе данных, исаользуя переданный в конструкторе конфиг.
     * @throws SQLException
     */
    @Override
    public void connect() throws SQLException {
        this.conn = DriverManager.getConnection(config.getConnection());
        SQLiteDB.log.info("Подключение к базе данных установлено.");
    }

    /**
     * Удаляет все таблицы базы данных.
     * @throws SQLException
     */
    @Override
    public void clearScheme() throws SQLException {
        Statement statement = conn.createStatement();
        for(String table : config.getTables()) {
            statement.executeUpdate("DROP TABLE \"%s\"".formatted(table));
        }
        statement.close();
    }

    @Override
    public void initScheme() throws SQLException {
        Statement statement = conn.createStatement();
        statement.executeUpdate(config.getDbScheme());
        statement.close();
    }

    /**
     * Завершает подключение к базе данных
     * @throws SQLException
     */
    @Override
    public void disconnect() throws SQLException {
        SQLiteDB.log.info("Подключение к базе данных разоравано.");
        conn.close();
    }
}