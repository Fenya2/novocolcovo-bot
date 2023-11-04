package new_core.service_handlers.handlers;

import models.Message;
import new_core.service_handlers.services.CloseOrderService;

/** Обработчик контекстов {@link models.UserState#ORDER_CLOSING_COURIER ORDER_CLOSING_COURIER}
 *  и {@link models.UserState#ORDER_CLOSING_CLIENT ORDER_CLOSING_CLIENT} */

public class HandlerCloseOrderService {

    /** @see CloseOrderService */
    private final CloseOrderService closeOrderSerivce;

    /** Конструктор*/
    public HandlerCloseOrderService(CloseOrderService closeOrderSerivce) {
        this.closeOrderSerivce = closeOrderSerivce;
    }

    /**Обработчик команд связаных с контекстом {@link models.UserState#ORDER_CLOSING_CLIENT ORDER_CLOSING_COURIER}
     и {@link models.UserState#ORDER_CLOSING_COURIER ORDER_CLOSING_COURIER }*/
    public void handle(Message msg) {
        if(msg.getText().charAt(0)=='/'){
            String message = "Команды сейчас недоступны";
            msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
        }
        else{
            String message = closeOrderSerivce.continueSession(msg.getUser().getId(),msg.getText());
            msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
        }
    }
}
