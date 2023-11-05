package core.service_handlers.handlers;

import core.service_handlers.services.ServiceManager;
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
                    "Я вас не понимаю, напишите команду. Список команд можно посмотреть вызвав команду /help";
            msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            return 1;
        }
        switch (msg.getText()) {
            case "/start" -> {
                String startMessage = serviceManager.start(msg);
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), startMessage);
                return 2;
            }
            case "/help" -> {
                String message = """
                        /edit_user - изменить имя и описание о себе.
                        /create_order - создать заказ.
                        /edit_order - изменить заказ.
                        /cancel_order - удалить заказ.
                        /show_order - посмотреть список созданных заказов.
                        /show_pending_orders - вывести список всех заказов, доступных для принятия.
                        /accept_order - принять заказ. Прежде чем принимать заказ, посмотрите список заказов(доступных для принятия), вызвав команду /show_pending_orders).
                        /show_accept_order - вывести список принятых заказов.
                        /close_order - завершить заказ.
                        """;
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                return 2;
            }
            case "/edit_user" -> {
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
        }
        String startMessage =
                "Такой команды не существует. Список команд можно посмотреть вызвав команду /help ";
        msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), startMessage);
        return 3;
    }
}
