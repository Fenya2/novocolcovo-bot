package config;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Класс конфигурации для базы данных (возможно не только sqlite) */
public class SQLDBconfig {
    /** Подключение, передаваемое DriverManager */
    private final String connection;

    /** Схема базы данных */
    private final String dbScheme;

    /** Имена таблиц в базе данных */
    private final List<String> tables;

    /** @param configure путь к файлу конфигурации типа json */
    public SQLDBconfig(String configure) {
        String str;
        try {
            str = FileUtils.readFileToString(new File(configure), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("something went wrong");
            throw new RuntimeException("file not founded");
        }
        JSONObject jo = new JSONObject(str);
        connection = jo.getString("connection");
        dbScheme = jo.getString("scheme");
        tables = new ArrayList<String>();

        JSONArray ja = jo.getJSONArray("tables");
        for(int i = 0; i < ja.length(); ++i) {
            tables.add(ja.getString(i));
        }
    }

    /**@return строка для подключения к бд. */
    public String getConnection() {
        return connection;
    }

    public String getDbScheme() {
        return dbScheme;
    }

    public List<String> getTables() {
        return tables;
    }
}