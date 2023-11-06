package core.service_handlers.handlers;

import core.MessageSender;
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

    /**Обработчик команд связаных с контекстом {@link models.UserState#ORDER_CLOSING_CLIENT ORDER_CLOSING_COURIER}
     {@link models.UserState#ORDER_CLOSING_COURIER ORDER_CLOSING_COURIER }
     */
    public void handle(Message msg) {
        switch (msg.getText()){
            case "/help"->{
                String message = "Ты сейчас находишься в контексте завершения заказа." +
                        "Напиши номер заказа, который ты уже отдал."+
                        "Если ты не знаешь этого номера, то тогда выйди из контекста, "+
                        "написав команду /cancel и вызови команду /show_pending_orders. "+
                        "Учти, что из команд доступны только /help и /cancel, на остальное я не смогу тебе ответить";;
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            }
            case "/cancel" -> {
                String message = closeOrderCourierService.cancel(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            }
            default -> {
                if (msg.getText().charAt(0) == '/'){
                    String message = "Прости, но я не знаю, что на это ответить. Вызови команду /help ";
                    msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                    return;
                }
                String message = closeOrderCourierService.continueSession(msg.getUser().getId(),msg.getText());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            }
        }
    }

}

