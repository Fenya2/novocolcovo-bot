package config;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SQLiteDBconfig {
    private final String name;
    private final ArrayList<String> tables;
    private final String location;
    public SQLiteDBconfig(String configFile) {
        String str;
        try {
            str = FileUtils.readFileToString(new File(configFile), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("something went wrong");
            throw new RuntimeException("file not founded");
        }
        JSONObject jo = new JSONObject(str);
        tables = new ArrayList<>();
        name = jo.getString("name");
        location = jo.getString("db_connection");
        JSONArray ja = jo.getJSONArray("tables");
        for(int i = 0; i < ja.length(); ++i)
            tables.add(ja.get(i).toString());
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getTables() {
        return tables;
    }

    public String getLocation() {
        return location;
    }
}
