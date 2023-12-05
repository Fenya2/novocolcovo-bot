package core.service_handlers.handlers;

import java.sql.SQLException;

import core.service_handlers.services.RateUserService;
import db.DBException;
import models.Message;

public class HandlerRateUserService {
    private RateUserService rateUserService;

    public HandlerRateUserService(RateUserService rateUserService) {
        this.rateUserService = rateUserService;
    }

    /**
     * Обрабатывает сообщение, предназначенное соответсвующему сервису.
     * Возврашает 1, если пришла команда, 2, если сообщение ушло сервису,
     * -1, если отправленное сообщение некорректно.
     * -2, если произошла ошибка со стороны сервиса.
     * @param message
     */
    public int handle(Message message) {
        if(message.getText().equals("/help")) {
            message.getBotFrom().sendTextMessage(
                message.getUserIdOnPlatform(),
                rateUserService.getHelpMessage()
            );
            return 1;
        }
        long newRate = 1;

        try {
            newRate = Long.valueOf(message.getText());
            if(newRate < 1 || newRate > 5) {
                throw new NumberFormatException("некорректный диапозон рейтинга");
            }
        }
        catch (NumberFormatException e) {
            message.getBotFrom().sendTextMessage(
                message.getUserIdOnPlatform(),
                "Некорректное значение. Введите целое число от 1 до 5."
            );
            return -1;
        }

        long ratingUser = message.getUserContext().getStateNum();
        try {
            rateUserService.updateUserRate(ratingUser, newRate);
            message.getBotFrom().sendTextMessage(message.getUserIdOnPlatform(), rateUserService.endSession(message.getUser().getId()));
        } catch (DBException | SQLException e) {
            message.getBotFrom().sendTextMessage(
                message.getUserIdOnPlatform(),
                "Проблемы с базой данных." + e.getMessage()
            );
            return -1;
        }
        return 2;
    }
}
