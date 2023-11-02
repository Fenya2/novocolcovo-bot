package new_core;

import models.Message;
import new_core.service_handlers.services.*;

public class CommandHandler {

    /**
     * Сервис изменения аккаунта пользователя.
     */
    private final EditUserService editUserService;
    private final ServiceManager serviceManager;

    public CommandHandler(EditUserService updateUserService, ServiceManager serviceManager) {
        this.editUserService = updateUserService;
        this.serviceManager = serviceManager;
    }

    /**
     * Осуществляет обработку команд, отправленных пользователем.
     * Если срабатывает этот метод, гарантируется, что контекста у пользователя нет, пользователь
     * в системе есть. Эти проверку сделал MessageHandler.
     *
     * @param msg  сообщение пользователя
     * @param user пользователь, изъятый из базы данных в MessageHandler, отправивший сообщение.
     */
    public void handle(Message msg) {
        if (msg.getText().charAt(0) != '/') {
            String message = "Я вас не понимаю, напишите команду. Список команд можно посмотреть вызвав команду /help";
            msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
        }
        switch (msg.getText()) {
            case "/case"->{
                String startMessage = serviceManager.start(msg);
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), startMessage);
            }
            case "/help" -> {
                String message = """
                        /create_order - создать заказ
                        /edit_order - изменить заказ
                        /cancel_order - удалить заказ
                        /show_order - посмотреть список заказов
                        """;
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            }
            case "/edit_user" -> {
                String startMessage = editUserService.startSession(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), startMessage);
            }
            case "/create_order" -> {
                String startMessage = serviceManager.startCreateOrder(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(),startMessage);
            }
            case "/edit_order" -> {
                String startMessage = serviceManager.startEditOrder(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(),startMessage);

            }
            case "/cancel_order" -> {
                String startMessage = serviceManager.startCancelOrder(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(),startMessage);
            }
            case "/show_order" -> {
                String startMessage = serviceManager.showOrder(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(),startMessage);
            }
        }
    }
}
