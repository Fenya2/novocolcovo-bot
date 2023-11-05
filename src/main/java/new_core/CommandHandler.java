package new_core;

import models.Message;

/** Обработчик команд. <br>
 * При вызове команды, она либо сразу обрабатывается,
 * либо запускается контекст, в который помещается пользователь для дальнейшей работы
 */
public class CommandHandler {

    /** @see ServiceManager */
    private final ServiceManager serviceManager;

    /** Конструктор {@link CommandHandler}*/
    public CommandHandler(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    /**
     * Осуществляет обработку команд, отправленных пользователем.
     * Если срабатывает этот метод, гарантируется, что контекста у пользователя нет, пользователь
     * в системе есть. Эту проверку сделал MessageHandler.
     *
     * @param msg  сообщение пользователя
     */
    public void handle(Message msg) {
        if (msg.getText().charAt(0) != '/') {
            String message =
                    "Я вас не понимаю, напишите команду. Список команд можно посмотреть вызвав команду /help";
            msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            return;
        }
        switch (msg.getText()) {
            case "/start"->{
                String startMessage = serviceManager.start(msg);
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), startMessage);
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
            }
            case "/edit_user" -> {
                String startMessage = serviceManager.startEditUserService(msg.getUser().getId());
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
            case "/show_pending_orders"->{
                String startMessage = serviceManager.showPendingOrders(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(),startMessage);
            }
            case "/accept_order"->{
                String startMessage = serviceManager.startAcceptOrder(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(),startMessage);
            }
            case "/show_accept_order"->{
                String startMessage = serviceManager.showAcceptOrder(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(),startMessage);
            }
            case "/close_order"->{
                String startMessage = serviceManager.startCloseOrder(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(),startMessage);
            }
            default -> {
                String startMessage =
                        "Такой команды не существует. Список команд можно посмотреть вызвав команду /help ";
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(),startMessage);
            }
        }
    }
}
