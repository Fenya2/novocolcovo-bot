package new_core.service_handlers.handlers;

import models.Message;
import new_core.service_handlers.services.CancelOrderService;

/** Обработчик контекста {@link models.UserState#ORDER_CANCELING ORDER_CANCELING}. */
public class HandlerCancelOrderService {

    /** @see CancelOrderService*/
    private final CancelOrderService cancelOrderService;

    /** Конструктор {@link HandlerCancelOrderService}*/
    public HandlerCancelOrderService(CancelOrderService cancelOrderService) {
        this.cancelOrderService = cancelOrderService;
    }

    /**Обработчик команд связаных с контекстом {@link models.UserState#ORDER_CANCELING ORDER_CANCELING}*/
    public void handle(Message msg) {
        if(msg.getText().charAt(0)=='/'){
            String message = "Команды сейчас недоступны";
            msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
        }
        else{
            String message = cancelOrderService.continueSession(msg.getUser().getId(),msg.getText());
            msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
        }

    }
}
