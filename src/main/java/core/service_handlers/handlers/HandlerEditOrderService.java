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
        switch (msg.getText()){
            case "/help"->{
                String message = "Ты сейчас находишься в контексте изменения заказа." +
                        "Выбери заказ и напиши новый список продуктов, который ты бы хотел видеть в заказе."+
                        "Учти, что из команд доступны только /help и /cancel, на остальное я не смогу тебе ответить";
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            }
            case "/cancel" -> {
                String message = editOrderService.cancel(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            }
            default -> {
                if (msg.getText().charAt(0) == '/'){
                    String message = "Прости, но я не знаю, что на это ответить. Вызови команду /help ";
                    msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                    return;
                }
                String message = editOrderService.continueSession(msg.getUser().getId(),msg.getText());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            }
        }
    }
}
