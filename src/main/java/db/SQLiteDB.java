package db;

import config.SQLiteDBconfig;
import models.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/*
    Реализация интерфейса DBinterface
 */
public class SQLiteDB implements DBinterface {
    private final Connection conn;
    private final SQLiteDBconfig config;

    public SQLiteDB(SQLiteDBconfig config) throws ClassNotFoundException, SQLException
    {
        this.config = config;
        Class.forName("org.sqlite.JDBC");
        this.conn = DriverManager.getConnection(config.getLocation()); // подключаемся к БД
        System.out.println("База Подключена!"); // сделать логер
        initialize(); // инициализируем таблицы, если их не было
    }

    private void initialize() throws SQLException {
        ArrayList<String> tables = config.getTables();
        Statement statmt;
        statmt = conn.createStatement();
        for (String table : tables) statmt.execute(table); // sql-запросы берем из файла конфига, который представлен в объекте config
    }
    @Override
    public void register(User user) {

    }

    @Override
    public void login(User user) {

    }

    @Override
    public void logout(User user) {

    }
}