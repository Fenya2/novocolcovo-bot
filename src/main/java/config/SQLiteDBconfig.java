package config;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** Класс конфигурации для базы данных (возможно не только sqlite) */
public class SQLiteDBconfig {
    /** Подключение, передуваемое DriverManager */
    private final String connection;

    /** @param configure путь к файлу конфигурации типа json */
    public SQLiteDBconfig(String configure) {
        String str;
        try {
            str = FileUtils.readFileToString(new File(configure), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("something went wrong");
            throw new RuntimeException("file not founded");
        }
        JSONObject jo = new JSONObject(str);
        connection = jo.getString("db_connection");
    }

    /**@return строка для подключения к бд. */
    public String getConnection() {
        return connection;
    }
}