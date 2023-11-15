package core;

import config.BotMessages;
import models.Message;

/**
 * Обработчик команд. <br>
 * При вызове команды, она либо сразу обрабатывается,
 * либо запускается контекст, в который помещается пользователь для дальнейшей работы
 */
public class CommandHandler {

    /** @see ServiceManager */
    private final ServiceManager serviceManager;

    /** Конструктор {@link CommandHandler} */
    public CommandHandler(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    /**
     * Осуществляет обработку команд, отправленных пользователем.
     * Если срабатывает этот метод, гарантируется, что контекста у пользователя нет, пользователь
     * в системе есть. Эту проверку сделал MessageHandler.
     *
     * @param msg сообщение пользователя
     * @return 1, если отправленное сообщение не команда. 2, если отправленная команда корректна,
     * 3, если отправленная команда некорректна.
     */
    public int handle(Message msg) {
        if (msg.getText().charAt(0) != '/') {
            String message =
                    "Прости, но я не знаю, что на это ответить. Вызови команду /help";
            msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            return 1;
        }
        switch (msg.getText()) {
            case "/start" -> {
                if(msg.getUser() == null) {
                    msg.getBotFrom().sendTextMessage(
                            msg.getUserIdOnPlatform(),
                            BotMessages.START_MESSAGE.getMessage()
                    );
                    return 2;
                }
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), BotMessages.HELP_MESSAGE.getMessage());
                return 2;
            }
            case "/register" -> {
                String response = serviceManager.register(msg);
                msg.getBotFrom().sendTextMessage(
                        msg.getUserIdOnPlatform(),
                        response
                );
                return 2;
            }
            case "/login" -> {
                String response = serviceManager.login(msg);
                msg.getBotFrom().sendTextMessage(
                        msg.getUserIdOnPlatform(),
                        response
                );
                return 2;
            }
            case "/help" -> {
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), BotMessages.HELP_MESSAGE.getMessage());
                return 2;
            }
            case "/profile" -> {
                String startMessage = serviceManager.startEditUserService(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), startMessage);
                return 2;
            }
            case "/create_order" -> {
                String startMessage = serviceManager.startCreateOrder(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), startMessage);
                return 2;
            }
            case "/edit_order" -> {
                String startMessage = serviceManager.startEditOrder(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), startMessage);
                return 2;
            }
            case "/cancel_order" -> {
                String startMessage = serviceManager.startCancelOrder(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), startMessage);
                return 2;
            }
            case "/show_order" -> {
                String startMessage = serviceManager.showOrder(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), startMessage);
                return 2;
            }
            case "/show_pending_orders" -> {
                String startMessage = serviceManager.showPendingOrders(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), startMessage);
                return 2;
            }
            case "/accept_order" -> {
                String startMessage = serviceManager.startAcceptOrder(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), startMessage);
                return 2;
            }
            case "/show_accept_order" -> {
                String startMessage = serviceManager.showAcceptOrder(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), startMessage);
                return 2;
            }
            case "/close_order" -> {
                String startMessage = serviceManager.startCloseOrder(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), startMessage);
                return 2;
            }
            default -> {
                String startMessage =
                    "Прости, но я не знаю, что на это ответить. Вызови команду /help ";
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), startMessage);
                return 3;
            }
        }
    }
}
