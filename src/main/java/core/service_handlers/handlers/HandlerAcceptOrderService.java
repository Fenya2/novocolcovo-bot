package core.service_handlers.handlers;

import core.MessageSender;
import models.Message;
import core.service_handlers.services.AcceptOrderService;

/** Обработчик контекстов {@link models.UserState#ORDER_ACCEPTING ORDER_ACCEPTING}*/
public class HandlerAcceptOrderService {

    /** @see AcceptOrderService */
    private final AcceptOrderService acceptOrderService;

    /** Конструктор {@link HandlerAcceptOrderService}*/
    public HandlerAcceptOrderService(AcceptOrderService acceptOrderService) {
        this.acceptOrderService = acceptOrderService;
    }

    /**Обработчик команд связаных с контекстом {@link models.UserState#ORDER_ACCEPTING ORDER_ACCEPTING}*/
    public void handle(Message msg) {
        if(msg.getText().charAt(0)=='/'){
            String message = "Команды сейчас недоступны";
            msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
        }
        else{
            String message = acceptOrderService.continueSession(msg.getUser().getId(),msg.getText());
            msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
        }
    }

}
