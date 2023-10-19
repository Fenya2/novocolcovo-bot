
package db;

import config.SQLiteDBconfig;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Реазизация интерфейса
 */
public class SQLiteDB implements DB{
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
    }

    /**
     * Сущность, передаваемая репозиториям для выполнения запросов к БД.
     */
    @Override
    public Connection getConnection() {
        return conn;
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