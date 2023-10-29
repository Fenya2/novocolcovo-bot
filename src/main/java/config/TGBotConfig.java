package config;

import models.Platform;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TGBotConfig {
    /** Имя бота, указываемое в файле конфигурации */
    private final String name;
    /** Токен бота, указываемый в файле конфигурации */
    private final String token;
    /** Платформа, на которой бот работает, указываемая в файле конфигурации */
    private final Platform platform;

    /** @param configFile путь к файлу конфигурации типа json. */
    public TGBotConfig(String configFile){
        String str;
        try {
            str = FileUtils.readFileToString(new File(configFile), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("something went wrong");
            throw new RuntimeException("file not founded");
        }
        JSONObject jo = new JSONObject(str);
        platform = Platform.TELEGRAM;
        name = jo.getString("name");
        token = jo.getString("token");
    }

    /** Возвращает имя бота */
    public String getName() {
        return name;
    }

    /** Возвращает токен бота */
    public String getToken() {
        return token;
    }

    /** Возвращает платформу, на которой работает бот. */
    public Platform getPlatform() {return platform;}
}