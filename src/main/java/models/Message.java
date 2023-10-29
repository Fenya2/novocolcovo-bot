package models;

/** Message - тип сообщений, с которым работает core логика проекта. */
public class Message {
    /** Платформа с которой пришло сообщение*/
    private Platform platform;
    /** Идентификатор отправителя сообщения на платформе, с которой он отправил сообщение */
    private String userIdOnPlatform;
    /** Текст сообщения */
    private String text;

    /**
     * поля text, platform, userIdOnPlatform выставляются в
     * "empty constructor text", {@link Platform NO_PLATFORM}, "empty constructor id"
     * соответственно.
     */
    public Message() {
        this.text = "empty constructor text";
        this.platform = Platform.NO_PLATFORM;
        this.userIdOnPlatform = "empty constructor id";
    }

    /** Конструктор для работы с telegram. */
    public Message(org.telegram.telegrambots.meta.api.objects.Message message) {
        this.platform = Platform.TELEGRAM;
        setText(message.getText());
        this.userIdOnPlatform = String.valueOf(message.getChatId());
    }

    /** Возвращает текст сообщения. */
    public String getText() {
        return text;
    }

    /**
     * @param text текст сообщения. не <b>null</b>.
     * @throws IllegalArgumentException
     */
    public void setText(String text) throws IllegalArgumentException {
        if(text == null)
            throw new IllegalArgumentException("incorrect text. Must be not null");
        this.text = text;
    }

    /** Возвращает платформу, с которой отправлено сообщение. */
    public Platform getPlatform() {
        return platform;
    }

    /** @param platform платформа. Не <b>null</b> */
    public void setPlatform(Platform platform) throws IllegalArgumentException{
        if(platform == null) {
            throw new IllegalArgumentException("platform must be not null");
        }
        this.platform = platform;
    }

    /** Возвращает идентификатор пользователя на платформе, с которой было отправлено сообщение. */
    public String getUserIdOnPlatform() {
        return userIdOnPlatform;
    }

    /**
     * @param userIdOnPlatform идентификатор пользователя на платформе, с которой он взаимодействует
     *                         с программой. Не <b>null</b>
     */
    public void setUserIdOnPlatform(String userIdOnPlatform) throws IllegalArgumentException {
        if(userIdOnPlatform == null)
            throw new IllegalArgumentException("incorrect userIdOnPlatform. Must be not null");
        this.userIdOnPlatform = userIdOnPlatform;
    }
}
