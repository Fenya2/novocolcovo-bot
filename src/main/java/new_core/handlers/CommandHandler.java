package new_core.handlers;

import models.Message;
import new_core.services.EditUserService;

public class CommandHandler {

    /** Сервис изменения аккаунта пользователя. */
    private final EditUserService editUserService;

    public CommandHandler(EditUserService editUserService) {
        this.editUserService = editUserService;
    }

    /**
     * Осуществляет обработку команд, отправленных пользователем.
     * Если срабатывает этот метод, гарантируется, что контекста у пользователя нет, пользователь
     * в системе есть. Эти проверку сделал MessageHandler.
     * @param message сообщение пользователя
     * @param user пользователь, изъятый из базы данных в MessageHandler, отправивший сообщение.
     */
    public void handle(Message message) {
        switch (message.getPlatform()) {
            case TELEGRAM -> {
                switch (message.getText()) {
                    case "/edit_user": {
                        String startMessage = editUserService.startSession(message.getUser().getId());
                        message.getBotFrom().sendTextMessage(message.getUserIdOnPlatform(), startMessage);
                        return;
                    }
                }
            }
        }
    }
}
