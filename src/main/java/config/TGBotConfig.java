package config;

import models.Platform;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Класс конфига телеграм бота /TODO очень похож на конфиг вк бота, мб имеем смысл унаследоваться.
 */
public class TGBotConfig {
    /**
     * Имя бота
     */
    private final String name;
    /**
     * Токен бота
     */
    private final String token;
    /**
     * Платформа, на которой бот работает
     */
    private final Platform platform;

    private final Map<String, String> apiMethods;

    /**
     * @param token токен бота
     */
    public TGBotConfig(String token, String configFilePath) {
        this.token = token;
        String str;
        try {
            str = FileUtils.readFileToString(new File(configFilePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("something went wrong");
            throw new RuntimeException("file not founded");
        }
        JSONObject jo = new JSONObject(str);
        name = jo.getString("name");
        platform = jo.getEnum(Platform.class, "platform");

        // TODO спросить, как сразу получить hashMap.
        apiMethods = new HashMap<>();
        jo = jo.getJSONObject("api_methods");
        Iterator<String> iterator = jo.keys();
        while (iterator.hasNext()) {
            String method = iterator.next();
            apiMethods.put(method, jo.getString(method));
        }
    }

    /**
     * Возвращает имя бота
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает токен бота
     */
    public String getToken() {
        return token;
    }

    /**
     * Возвращает платформу, на которой работает бот.
     */
    public Platform getPlatform() {
        return platform;
    }

    public Map<String, String> getApiMethods() {
        return apiMethods;
    }
}