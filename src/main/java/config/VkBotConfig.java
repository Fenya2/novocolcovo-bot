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

public class VkBotConfig {
    /** Имя бота */
    private final String name;
    /** токен для взаимодействия с ботом */
    private final String token;
    /** Платформа, на которой работает бот */
    private final Platform platform;

    /** Map, хранящий post запросы к vk api. */
    private final Map<String, String> apiMethods;

    /**
     * @param envVarName переменная окружения, хранящая токен.
     * @param configFilePath путь к файлу конфигурации
     */
    public VkBotConfig(String envVarName, String configFilePath) {
        this.token = envVarName;
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

    /** @return имя бота */
    public String getName() {
        return name;
    }

    /** @return токен бота */
    public String getToken() {
        return token;
    }

    /** @return платформа, на которой работает бот */
    public Platform getPlatform() {
        return platform;
    }

    public Map<String, String> getApiMethods() {
        return apiMethods;
    }
}
