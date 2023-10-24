package models;

public class Message {
    private String userIdOnPlatform;
    private String text;
    private String platform;

    public Message() {
        this.text = "";
        this.platform = "";
        this.userIdOnPlatform = "";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUserIdOnPlatform() {
        return userIdOnPlatform;
    }

    public void setUserIdOnPlatform(String userIdOnPlatform) {
        this.userIdOnPlatform = userIdOnPlatform;
    }
}
