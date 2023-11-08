package config;

import models.Platform;

public class VkBotConfig {
    /** Имя бота */
    private final String name;
    /** токен для взаимодействия с ботом */
    private final String token;
    /** Платформа, на которой работает бот */
    private final Platform platform;

    /**
     * @param token токен бота
     */
    public VkBotConfig(String token) {
        this.name = "novocolcovo_bot";
        this.token = token;
        this.platform = Platform.VK;
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
}
