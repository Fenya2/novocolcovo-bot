
package db;

import config.SQLiteDBconfig;
import org.apache.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Реазизация интерфейса
 */
public class SQLiteDB implements DB{
    private static final Logger log = Logger.getLogger(SQLiteDB.class.getName());

    /**
     * Объект подключения к БД.
     */
    private final Connection conn;
    private final SQLiteDBconfig config;

    /**
     * Настройка БД на основе конфига
     */
    public SQLiteDB(SQLiteDBconfig config) throws SQLException, ClassNotFoundException {
        this.config = config;
        Class.forName("org.sqlite.JDBC");
        this.conn = DriverManager.getConnection(config.getConnection());
        SQLiteDB.log.info("Подключение к базе данных установлено.");
    }

    /**
     * Сущность, передаваемая репозиториям для выполнения запросов к БД.
     */

    @Override
    public Statement getStatement() throws SQLException {
        return conn.createStatement();
    }

    /**
     * Простая проверка, создана ли новый файл базы данных, или запущен старый.
     * @return true если создана новая БД, иначе false.
     */
    public boolean isEmpty() {
        File db_file = new File(config.getLocation());
        return db_file.length() == 0;
    }
}