package new_core.service_handlers.handlers;

import models.Message;
import new_core.service_handlers.services.CloseOrderClientService;

/** Обработчик контекстов {@link models.UserState#ORDER_CLOSING_CLIENT ORDER_CLOSING_CLIENT} */
public class HandlerCloseOrderClientService {

    /** @see CloseOrderClientService */
    private final CloseOrderClientService closeOrderClientService;

    /** Конструктор {@link HandlerCloseOrderClientService}*/
    public HandlerCloseOrderClientService(CloseOrderClientService closeOrderClientService) {
        this.closeOrderClientService = closeOrderClientService;
    }

    /**Обработчик команд связаных с контекстом {@link models.UserState#ORDER_CLOSING_CLIENT ORDER_CLOSING_COURIER}
     {@link models.UserState#ORDER_CLOSING_COURIER ORDER_CLOSING_COURIER }
     */
    public void handle(Message msg) {
        if(msg.getText().charAt(0)=='/'){
            String message = "Команды сейчас недоступны";
            msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
        }
        else{
            String message = closeOrderClientService.continueSession(msg.getUser().getId(),msg.getText());
            msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
        }
    }
}
