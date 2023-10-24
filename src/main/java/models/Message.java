package models;

public class Message {
    private String userIdOnPlatform;
    private String text;
    private String platform;

    public Message() {
        this.text = "empty constructor text";
        this.platform = "telegram";
        this.userIdOnPlatform = "empty constructor id";
    }

    public Message(org.telegram.telegrambots.meta.api.objects.Message message) {
        setText(message.getText());
        this.platform = "telegram";
        this.userIdOnPlatform = String.valueOf(message.getChatId());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if(text == null) {
            throw new IllegalArgumentException("incorrect text. Must be not null");
        }
        this.text = text;
    }

    public String getPlatform() {
        return platform;
    }

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
