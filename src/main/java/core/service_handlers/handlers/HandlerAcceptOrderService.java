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
        switch (msg.getText()){
            case "/help"->{
                String message = "Ты сейчас находишься в контексте принятия заказа." +
                        "Напиши номер заказа который ты хотел бы принять."+
                        "Если ты не знаешь этого номера, то тогда выйди из контекста,"+
                        "написав команду /cancel и вызови команду /show_pending_orders"+
                        "Учти, что из команд доступны только /help и /cancel, на остальное я не смогу тебе ответить";
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            }
            case "/cancel" ->acceptOrderService.cancel(msg.getUser().getId());
            default -> {
                if (msg.getText().charAt(0) == '/'){
                    String message = "Прости, но я не знаю, что на это ответить. Вызови команду /help ";
                    msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                    return;
                }
                String message = acceptOrderService.continueSession(msg.getUser().getId(),msg.getText());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            }
        }
    }

}
