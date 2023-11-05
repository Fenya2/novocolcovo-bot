package core.service_handlers.handlers;

import models.Message;
import core.service_handlers.services.EditOrderService;

/** Обработчик контекста {@link models.UserState#ORDER_EDITING ORDER_EDITING}. */
public class HandlerEditOrderService {

    /** @see EditOrderService */
    private final EditOrderService editOrderService;

    /** Конструктор {@link HandlerEditOrderService}*/
    public HandlerEditOrderService(EditOrderService editOrderService) {
        this.editOrderService = editOrderService;
    }

    /**Обработчик команд связаных с контекстом {@link models.UserState#ORDER_EDITING ORDER_EDITING}*/
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
