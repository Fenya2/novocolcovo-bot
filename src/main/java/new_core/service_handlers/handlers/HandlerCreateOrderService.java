package new_core.service_handlers.handlers;

import models.Message;
import new_core.service_handlers.services.CreateOrderService;

public class HandlerCreateOrderService {
    private final CreateOrderService createOrderService;

    public HandlerCreateOrderService(CreateOrderService createOrderService) {
        this.createOrderService = createOrderService;
    }

    public void handle(Message msg) {
        if(msg.getText().charAt(0)=='/'){
            String message = "Команды сейчас недоступны";
            msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
        }
        else{
            String message = createOrderService.continueSession(msg.getUser().getId(),msg.getText());
            msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
        }

    }
}
