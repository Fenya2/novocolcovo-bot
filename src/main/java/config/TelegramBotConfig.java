package config;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TelegramBotConfig {
    private final String name;
    private final String token;
    public TelegramBotConfig(String configFile){
        String str;
        try {
            str = FileUtils.readFileToString(new File(configFile), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("something went wrong");
            throw new RuntimeException("file not founded");
        }
        JSONObject jo = new JSONObject(str);
        name = jo.getString("name");
        token = jo.getString("token");
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }
}