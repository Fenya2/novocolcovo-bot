package core.service_handlers.handlers;

import models.Message;

import core.service_handlers.services.CreateOrderService;

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
        switch (msg.getText()){
            case "/help"->{
                String message = "Ты сейчас находишься в контексте создания заказа." +
                        "Напиши продукты которые ты хотел бы внести в сой заказ."+
                        "Учти, что из команд доступны только /help и /cancel, на остальное я не смогу тебе ответить";
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            }
            case "/cancel" -> {
                String message = createOrderService.cancel(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            }

            default -> {
                if (msg.getText().charAt(0) == '/'){
                    String message = "Прости, но я не знаю, что на это ответить. Вызови команду /help ";
                    msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                    return;
                }
                String message = createOrderService.continueSession(msg.getUser().getId(),msg.getText());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            }
        }
    }
}
