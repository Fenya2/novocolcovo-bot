package config;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Класс конфигурации для SQliteDB
 */
public class SQLiteDBconfig {
    private final String dbName;
    /**
     * Подключение, пеоедваемое DriverManager
     */
    private final String connection;

    /**
     * Местоположение файла БД на диске.*
     */
    private final String location;

    public SQLiteDBconfig(String configure) {
        String str;
        try {
            str = FileUtils.readFileToString(new File(configure), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("something went wrong");
            throw new RuntimeException("file not founded");
        }
        JSONObject jo = new JSONObject(str);
        dbName = jo.getString("name");
        connection = jo.getString("db_connection");
        location = jo.getString("db_location");
    }

    public String getDbName() {
        return dbName;
    }

    public String getConnection() {
        return connection;
    }

    public String getLocation() {
        return location;
    }
}