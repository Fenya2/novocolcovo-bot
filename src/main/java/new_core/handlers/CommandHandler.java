package new_core.handlers;

import models.Message;
import models.User;
import models.UserContext;
import new_core.services.UpdateUserService;

public class CommandHandler {

    /** Сервис изменения аккаунта пользователя. */
    private final UpdateUserService updateUserService;

    public CommandHandler(UpdateUserService updateUserService) {
        this.updateUserService = updateUserService;
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
                        String startMessage = updateUserService.startSession(message.getUser().getId());
                        message.getBotFrom().sendTextMessage(message.getUserIdOnPlatform(), startMessage);
                        return;
                    }
                }
            }
        }
    }
}
