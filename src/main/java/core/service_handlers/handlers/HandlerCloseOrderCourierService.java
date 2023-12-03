package core.service_handlers.handlers;

import core.service_handlers.services.CloseOrderCourierService;
import models.Message;

/** Обработчик контекстов {@link models.UserState#ORDER_CLOSING_COURIER ORDER_CLOSING_COURIER}*/

public class HandlerCloseOrderCourierService {

    /** @see CloseOrderCourierService */
    private final CloseOrderCourierService closeOrderCourierService;

    /** Конструктор*/
    public HandlerCloseOrderCourierService(CloseOrderCourierService closeOrderService) {
        this.closeOrderCourierService = closeOrderService;
    }

    /**Обработчик команд связаных с контекстом
     * {@link models.UserState#ORDER_CLOSING_COURIER ORDER_CLOSING_COURIER}
     * @return
     * 1, если текст переданного сообщения - коррекстная команда
     * 2, если текст переданного сообщения - не команда и оно должно обрабатываться в сервисе.
     * 3, если текст переданного сообщение - некорректная команда.
     */
    public int handle(Message msg) {
        switch (msg.getText()){
            case "/help"->{
                String message = "Ты сейчас находишься в контексте завершения заказа." +
                        "Напиши номер заказа, который ты уже отдал."+
                        "Если ты не знаешь этого номера, то тогда выйди из контекста, "+
                        "написав команду /cancel и вызови команду /show_accept_orders. "+
                        "Учти, что из команд доступны только /help и /cancel, на остальное я не смогу тебе ответить";;
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                return 1;
            }
            case "/cancel" -> {
                String message = closeOrderCourierService.cancel(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                msg.getBotFrom().sendMainMenu(
                        msg.getUserIdOnPlatform(),
                        "Вы попали в главное меню. Выберите действие"
                );
                return 1;
            }
            default -> {
                if (msg.getText().charAt(0) == '/'){
                    String message = "Прости, но я не знаю, что на это ответить. Вызови команду /help ";
                    msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                    return 3;
                }
                String message = closeOrderCourierService.continueSession(msg.getUser().getId(),msg.getText());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                if (msg.getUserContext().getStateNum()==1){
                    msg.getBotFrom().sendMainMenu(
                            msg.getUserIdOnPlatform(),
                            "Вы попали в главное меню. Выберите действие"
                    );
                }
                return 2;
            }
        }
    }

}

