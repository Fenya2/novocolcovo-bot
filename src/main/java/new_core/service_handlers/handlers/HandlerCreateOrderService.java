package new_core.service_handlers.handlers;

import models.Message;
import new_core.service_handlers.services.CreateOrderService;
import new_core.service_handlers.services.EditUserService;

/** Обработчик контекста {@link models.UserState#ORDER_CREATING ORDER_CREATING}*/
public class HandlerCreateOrderService {

    /** @see CreateOrderService */
    private final CreateOrderService createOrderService;

    /** Конструктор {@link HandlerCreateOrderService}*/
    public HandlerCreateOrderService(CreateOrderService createOrderService) {
        this.createOrderService = createOrderService;
    }

    /**Обработчик команд связаных с контекстом {@link models.UserState#ORDER_CREATING ORDER_CREATING}*/
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
