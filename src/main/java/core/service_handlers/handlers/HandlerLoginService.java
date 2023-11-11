package core.service_handlers.handlers;

import core.service_handlers.services.LoginService;
import core.service_handlers.services.RegistrationService;
import models.Message;

/** Обработчик контекста {@link models.UserState#LOGGED LOGGED}. */
public class HandlerLoginService {

    /** @see RegistrationService */
    private final LoginService loginService;

    /** Конструктор {@link HandlerLoginService}*/
    public HandlerLoginService(LoginService loggedService) {
        this.loginService = loggedService;
    }

    /**
     * Обработчик команд связаных с контекстом {@link models.UserState#LOGGED LOGGED}
     * */
    public int handle(Message msg) {
        if(msg.getText().length()>5){
            String message = "Прости, но я не знаю, что на это ответить. Вызови команду /help";
            msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
            return 3;
        }
        switch (msg.getText()){
            case "/help"->{
                String message = "Введи код подтверждения";
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                return 1;
            }
            case "/login"->{
                String message = loginService.start(msg);
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                return 1;
            }
            default -> {
                if (msg.getText().charAt(0) == '/'){
                    String message = "Прости, но я не знаю, что на это ответить. Вызови команду /help ";
                    msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                    return 3;
                }
                String message = loginService.continueSession(msg);
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(), message);
                return 2;
            }
        }
    }
}
