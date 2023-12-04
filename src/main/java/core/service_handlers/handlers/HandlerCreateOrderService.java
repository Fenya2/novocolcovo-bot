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

    /**
     * Обработчик команд связаных с контекстом {@link models.UserState#ORDER_CREATING ORDER_CREATING}
     * @return
     * 1, если введенное сообщение является корректной командой в контексте сервиса
     * 2, если введенное сообщение - не команда в контексте сервиса и должна обрабатыватьсят сервсом.
     * 3, если введенное сообщение не является корректной командой в контексте сервиса.
     */
    public int handle(Message msg) {
        switch (msg.getText()){
            case "/help"->{
                String message = "Ты сейчас находишься в контексте создания заказа." +
                        "Напиши продукты которые ты хотел бы внести в сой заказ."+
                        "Учти, что из команд доступны только /help и /cancel, на остальное я не смогу тебе ответить";
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                return 1;
            }
            case "/cancel" -> {
                String message = createOrderService.cancel(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                msg.getBotFrom().sendMainMenu(
                        msg.getUserIdOnPlatform(),
                        "Вы попали в главное меню. Выберите действие"
                );
                return 1;
            }

            default -> {
                if (msg.getText().charAt(0) == '/'){
                    String message = "Прости, но я не знаю, что на это ответить. Вызови команду /help ";
                    msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                    return 3;
                }
                String message = createOrderService.continueSession(msg.getUser().getId(),msg.getText());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                msg.getBotFrom().sendMainMenu(
                        msg.getUserIdOnPlatform(),
                        "Вы попали в главное меню. Выберите действие"
                );
                return 2;
            }
        }
    }
}
