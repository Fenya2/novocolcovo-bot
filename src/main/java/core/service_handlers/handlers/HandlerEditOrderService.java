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

    /**
     * Обработчик команд связаных с контекстом {@link models.UserState#ORDER_EDITING ORDER_EDITING}
     * @return 1, если текст сообщения - корректная команда, которую можно обработать
     * 2, если текст сообщения не команда и будет обрабатываеться сервисом,
     * 3, если текст является некорректной командой или не привязан к какому-либо внутреннему
     * контексту сервиса.
     */
    public int handle(Message msg) {
        switch (msg.getText()){
            case "/help"->{
                String message = "Ты сейчас находишься в контексте изменения заказа." +
                        "Выбери заказ и напиши новый список продуктов, который ты бы хотел видеть в заказе."+
                        "Учти, что из команд доступны только /help и /cancel, на остальное я не смогу тебе ответить";
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                return 1;
            }
            case "/cancel" -> {
                String message = editOrderService.cancel(msg.getUser().getId());
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
                String message = editOrderService.continueSession(msg.getUser().getId(),msg.getText());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                if (msg.getUserContext().getStateNum()==1){
                    msg.getBotFrom().sendMainMenu(
                            msg.getUserIdOnPlatform(),
                            "Вы попали в главное меню. Выберите действие"
                    );
                }

                return 2;
            }
        }
    }
}
