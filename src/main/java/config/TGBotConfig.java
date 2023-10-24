package config;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TGBotConfig {
    private final String name;
    private final String token;
    private final String platform;

    public TGBotConfig(String configFile){
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
        platform = jo.getString("platform");
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    public String getPlatform() {return platform;}
}