package core.service_handlers.handlers;

import models.Message;
import core.service_handlers.services.CancelOrderService;

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
        switch (msg.getText()){
            case "/help"->{
                String message = "Ты сейчас находишься в контексте удаления заказа." +
                        "Напиши номер заказа, который ты бы хотел удалить."+
                        "Учти, что из команд доступны только /help и /cancel, на остальное я не смогу тебе ответить";;
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            }
            case "/cancel" -> {
                String message = cancelOrderService.cancel(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            }

            default -> {
                if (msg.getText().charAt(0) == '/'){
                    String message = "Прости, но я не знаю, что на это ответить. Вызови команду /help ";
                    msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                    return;
                }
                String message = cancelOrderService.continueSession(msg.getUser().getId(),msg.getText());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            }
        }
    }
}
