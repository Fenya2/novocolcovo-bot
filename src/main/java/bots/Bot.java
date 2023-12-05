package bots;

public interface Bot {
    void sendTextMessage(String recipient_id, String message);
    void sendMainMenu(String recipient_id, String message);
}
