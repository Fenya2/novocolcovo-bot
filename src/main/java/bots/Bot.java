package bots;

public interface Bot {
    public void sendTextMessage(String recipient_id, String message);
    public String getDomainByUserIdOnPlatform(String userIdOnPlatform);
}
