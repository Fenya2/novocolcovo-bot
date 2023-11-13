package core.service_handlers.handlers;

import core.service_handlers.services.RegistrationService;
import models.Message;

/** Обработчик контекста {@link models.UserState#REGISTRATION REGISTRATION}. */
public class HandlerRegistrationService {

    /** @see RegistrationService */
    private final RegistrationService registrationService;

    /** Конструктор {@link HandlerRegistrationService}*/
    public HandlerRegistrationService(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    /**
     * Обработчик команд связаных с контекстом {@link models.UserState#REGISTRATION REGISTRATION}
     */
    public int handle(Message msg) {
        switch (msg.getText()){
            case "/help"->{
                String message = "Ты сейчас находишься в контексте регистрации." +
                        "Ответь на вопросы, что бы я знал кто ты."+
                        "Учти, что из команд доступна только /help, на остальное я не смогу тебе ответить";
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                return 1;
            }
            default -> {
                if (msg.getText().charAt(0) == '/'){
                    String message = "Прости, но я не знаю, что на это ответить. Вызови команду /help ";
                    msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                    return 3;
                }
                String message = registrationService.continueSession(msg.getUser().getId(),msg.getText());
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                return 2;
            }
        }
    }
}
