package core.service_handlers.handlers;

import core.service_handlers.services.LoginService;
import db.DBException;
import models.Domain;
import models.Message;

import java.sql.SQLException;

/**
 * Обработчик сервиса авторизации
 */
public class HandlerLoginService {
    private LoginService loginService;

    public HandlerLoginService(LoginService loginService) {
        this.loginService = loginService;
    }
    public int handle(Message message, Domain domain) {
        // обработка команд
        switch (message.getText()) {
            case "/help": {
                message.getBotFrom().sendTextMessage(
                        message.getUserIdOnPlatform(),
                        "Вы находитесь в контексте авторизации. Для выхода напишите /cancel");
                return 1;
            }
            case "/cancel": {
                try {
                    loginService.cancelSession(message.getPlatform(), message.getUserIdOnPlatform());
                    message.getBotFrom().sendTextMessage(
                            message.getUserIdOnPlatform(),
                            "Вы вышли из режима авторизации.");
                } catch (DBException e) {
                    message.getBotFrom().sendTextMessage(
                            message.getUserIdOnPlatform(),
                            "Проблемы с базой данных" + e.getMessage());
                    return -1;
                }
                return 1;
            }
        }

        String response;
        try {
            response = loginService.continueSession(
                    message.getPlatform(),
                    message.getUserIdOnPlatform(),
                    message.getText(),
                    domain
            );
            message.getBotFrom().sendTextMessage(
                    message.getUserIdOnPlatform(),
                    response
            );
            return 2;
        } catch (DBException | SQLException e) {
            message.getBotFrom().sendTextMessage(
                    message.getUserIdOnPlatform(),
                    "Проблемы с базой данных" + e.getMessage()
            );
            return -1;
        }
    }
}
