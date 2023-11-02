package new_core.service_handlers.handlers;

import models.Message;
import new_core.service_handlers.services.CancelOrderService;

public class HandlerCancelOrderService {
    private final CancelOrderService cancelOrderService;

    public HandlerCancelOrderService(CancelOrderService cancelOrderService) {
        this.cancelOrderService = cancelOrderService;
    }

    public void handle(Message msg) {
        if(msg.getText().charAt(0)=='/'){
            String message = "Команды сейчас недоступны";
            msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
        }
        else{
            String message = cancelOrderService .continueSession(msg.getUser().getId(),msg.getText());
            msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
        }

    }
}
