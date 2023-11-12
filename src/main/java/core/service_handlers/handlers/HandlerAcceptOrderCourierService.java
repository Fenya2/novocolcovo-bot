package core.service_handlers.handlers;

import models.Message;
import core.service_handlers.services.AcceptOrderCourierService;

/** Обработчик контекстов {@link models.UserState#ORDER_ACCEPTING_COURIER ORDER_ACCEPTING}*/
public class HandlerAcceptOrderCourierService {

    /** @see AcceptOrderCourierService */
    private final AcceptOrderCourierService acceptOrderCourierService;

    /** Конструктор {@link HandlerAcceptOrderCourierService}*/
    public HandlerAcceptOrderCourierService(AcceptOrderCourierService acceptOrderService) {
        this.acceptOrderCourierService = acceptOrderService;
    }

    /**Обработчик команд связаных с контекстом {@link models.UserState#ORDER_ACCEPTING_COURIER ORDER_ACCEPTING}
     * @return 1, если текст сообщения - корректная команда, которую можно обработать
     * 2, если текст сообщения не команда и будет обрабатываеться сервисом,
     * 3, если текст является некорректной командой или не привязан к какому-либо внутреннему
     * контексту сервиса.
     */
    public int handle(Message msg) {
        switch (msg.getText()){
            case "/help"->{
                String message = "Ты сейчас находишься в контексте принятия заказа." +
                        "Напиши номер заказа который ты хотел бы принять."+
                        "Если ты не знаешь этого номера, то тогда выйди из контекста,"+
                        "написав команду /cancel и вызови команду /show_pending_orders"+
                        "Учти, что из команд доступны только /help и /cancel, на остальное я не смогу тебе ответить";
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                return 1;
            }
            case "/cancel" -> {
                String message = acceptOrderCourierService.cancel(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                return 1;
            }
            default -> {
                if (msg.getText().charAt(0) == '/'){
                    String message = "Прости, но я не знаю, что на это ответить. Вызови команду /help ";
                    msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                    return 3;
                }
                String message = acceptOrderCourierService.continueSession(msg.getUser().getId(),msg.getText());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                return 2;
            }
        }
    }
}
