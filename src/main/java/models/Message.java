package models;

/**
 * Message - то что получает логика проекта.
 */
public class Message {
    /**
     * платформа с которой пришло сообщение
     */
    private String platform;
    /**
     * id на этой платформе
     */
    private String userIdOnPlatform;
    /**
     * текст сообщения
     */
    private String text;

    public Message() {
        this.text = "empty constructor text";
        this.platform = "telegram";
        this.userIdOnPlatform = "empty constructor id";
    }

    /**
     * Конструктор для работы с telegram
     */
    public Message(org.telegram.telegrambots.meta.api.objects.Message message) {
        setText(message.getText());
        this.platform = "telegram";
        this.userIdOnPlatform = String.valueOf(message.getChatId());
    }

    public String getText() {
        return text;
    }

    /**
     * после проверки на нулевой текст меняет его на переданый
     */
    public void setText(String text) {
        if(text == null) {
            throw new IllegalArgumentException("incorrect text. Must be not null");
        }
        this.text = text;
    }

    public String getPlatform() {
        return platform;
    }

    /**
     * после проверки на наличие переданной платформы изменяет старую платформу на переданную
     */
    public void setPlatform(String platform) {
        switch (platform) {
            case "telegram":
                this.platform = "telegram";
        }
        throw new IllegalArgumentException("incorrect platform entered.");
    }

    public String getUserIdOnPlatform() {
        return userIdOnPlatform;
    }

    public void setUserIdOnPlatform(String userIdOnPlatform) {
        this.userIdOnPlatform = userIdOnPlatform;
    }
}
