package new_core.service_handlers.handlers;

import models.Message;
import new_core.service_handlers.services.CancelOrderService;
import new_core.service_handlers.services.EditOrderService;

public class HandlerEditOrderService {
    private final EditOrderService editOrderService;

    public HandlerEditOrderService(EditOrderService editOrderService) {
        this.editOrderService = editOrderService;
    }

    public void handle(Message msg) {
        if(msg.getText().charAt(0)=='/'){
            String message = "Команды сейчас недоступны";
            msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
        }
        else{
            String message = editOrderService.continueSession(msg.getUser().getId(),msg.getText());
            msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
        }

    }
}
