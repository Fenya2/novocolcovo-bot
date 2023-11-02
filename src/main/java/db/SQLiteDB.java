
package db;

import config.SQLiteDBconfig;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Реазизация интерфейса */
public class SQLiteDB implements DB{
    private static final Logger log = Logger.getLogger(SQLiteDB.class.getName());

    /** Объект подключения к бд. */
    private final Connection conn;

    /** @param config путь к файлу конфигурации бд. */
    public SQLiteDB(SQLiteDBconfig config) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        this.conn = DriverManager.getConnection(config.getConnection());
        SQLiteDB.log.info("Подключение к базе данных установлено.");
    }

    /** Сущность, передаваемая репозиториям для выполнения запросов к БД. */
    @Override
    public Statement getStatement() throws SQLException {
        return conn.createStatement();
    }
}