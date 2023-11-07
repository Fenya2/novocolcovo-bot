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

    /**
     * Обработчик команд связаных с контекстом {@link models.UserState#ORDER_CLOSING_CLIENT ORDER_CLOSING_CLIENT}
     *
     * @return
     * 1, если введенное сообщение является корректной командой в контексте сервиса
     * 2, если введенное сообщение должно обрабатываться сервисом.
     * 3, если введенное сообщение не является корректной командой в контексте сервиса.
     */
    public int handle(Message msg) {

        switch (msg.getText()){
            case "/help"->{
                String message = "Ты сейчас находишься в контексте завершения заказа." +
                        "Просто напиши закрыт заказ или нет, /yes или /no соответственно и я от тебя отстану."+
                        "Учти, что из команд доступны только /help , /yes и /no, на остальное я не смогу тебе ответить";
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                return 1;
            }
            case "/yes", "/no" -> { //todo @adfips чтобы не делать 2 проверки (здесь и в сервисе) лучше сделать 2 публичных метода в сервисе и вызывать их в зависимости от результата проверки здесь.
                String message = closeOrderClientService.continueSession(msg.getUser().getId(),msg.getText());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                return 2;
            }
            default -> {
                if(msg.getText().charAt(0) == '/'){
                    String message = closeOrderClientService.continueSession(msg.getUser().getId(),msg.getText());
                    msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                    return 2;
                }
                String message = "Прости, но я не знаю, что на это ответить. Вызови команду /help ";
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                return 3;
            }
        }
    }
}
