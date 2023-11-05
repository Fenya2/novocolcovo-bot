package config;

import models.Platform;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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

    /**
     * @param token токен бота
     */
    public TGBotConfig(String token) {
        this.name = "novocolcovo_bot";
        this.token = token;
        this.platform = Platform.TELEGRAM;
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
}