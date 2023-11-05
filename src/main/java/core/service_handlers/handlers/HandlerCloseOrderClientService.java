package core.service_handlers.handlers;

import models.Message;
import core.service_handlers.services.CloseOrderClientService;

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

        if(msg.getText().equals("/yes") || msg.getText().equals("/no")){
            String message = closeOrderClientService.continueSession(msg.getUser().getId(),msg.getText());
            msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            return;
        }
        switch (msg.getText()){
            case "/help"->{
                String message = "Ты сейчас находишься в контексте заверения заказа." +
                        "Просто напиши закрыт заказ или нет, /yes или /no соответственно и я от тебя отстану."+
                        "Учти, что из команд доступны только /help , /yes и /no, на остальное я не смогу тебе ответить";
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            }
            case "/yes", "/no" -> {
                String message = closeOrderClientService.continueSession(msg.getUser().getId(),msg.getText());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            }
            default -> {
                String message = "Прости, но я не знаю, что на это ответить. Вызови команду /help ";
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            }
        }
    }
}
