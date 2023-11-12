package core.service_handlers.handlers;

import core.service_handlers.services.AcceptOrderClientService;
import models.Message;

/**
 * Обработчик контекстов {@link models.UserState#ORDER_ACCEPTING_CLIENT ORDER_ACCEPTING_CLIENT}
 */
public class HandlerAcceptOrderClientService {

    /**
     * @see AcceptOrderClientService
     */
    private final AcceptOrderClientService acceptOrderClientService;

    /**
     * Конструктор {@link HandlerAcceptOrderClientService}
     */
    public HandlerAcceptOrderClientService(AcceptOrderClientService acceptOrderClientService) {
        this.acceptOrderClientService = acceptOrderClientService;
    }

    /**
     * Обработчик команд связаных с контекстом {@link models.UserState#ORDER_CLOSING_CLIENT ORDER_CLOSING_CLIENT}
     *
     * @return 1, если введенное сообщение является корректной командой в контексте сервиса
     * 2, если введенное сообщение должно обрабатываться сервисом.
     * 3, если введенное сообщение не является корректной командой в контексте сервиса.
     */
    public int handle(Message msg) {

        switch (msg.getText()) {
            case "/help" -> {
                String message = "Ты сейчас находишься в контексте принятия заказа." +
                        "Напиши отдашь ли ты свой заказ этому курьеру или нет, /yes или /no соответственно." +
                        "Учти, что из команд доступны только /help , /yes и /no, на остальное я не смогу тебе ответить";
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                return 1;
            }
            case "/yes", "/no" -> { //todo @adfips чтобы не делать 2 проверки (здесь и в сервисе) лучше сделать 2 публичных метода в сервисе и вызывать их в зависимости от результата проверки здесь.
                String message = acceptOrderClientService.continueSession(msg.getUser().getId(), msg.getText());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                return 2;
            }
            default -> {
                if (msg.getText().charAt(0) == '/') {
                    String message = acceptOrderClientService.continueSession(msg.getUser().getId(), msg.getText());
                    msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                    return 3;
                }
                String message = "Прости, но я не знаю, что на это ответить. Вызови команду /help";
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                return 2;
            }
        }
    }
}
