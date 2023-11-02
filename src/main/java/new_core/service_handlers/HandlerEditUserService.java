package new_core.service_handlers;

import config.services.UpdateUserServiceConfig;
import models.Message;
import new_core.service_handlers.services.EditUserService;

import java.sql.SQLException;

public class HandlerEditUserService {
    private final EditUserService updateUserService;
    public HandlerEditUserService(EditUserService updateUserService) {
        this.updateUserService = updateUserService;
    }

    /**
     * Обрабатывает сообщение пользователя, когда тот находится в контексте обновления аккаунта
     * пользователя.
     * @param message
     */
    public void handle(Message message) {
        switch (message.getText()) {
            case "/edit_username": {
                try {
                    updateUserService.setEditUsernameContext(message.getUser().getId());
                } catch (SQLException e) {
                    message.getBotFrom().sendTextMessage(
                            message.getUserIdOnPlatform(),
                            "Проблемы с базой данных" +
                                    e.getMessage()
                    );
                }
                message.getBotFrom().sendTextMessage(
                        message.getUserIdOnPlatform(),
                        UpdateUserServiceConfig.EDIT_USER_MESSAGE.getStr()
                );
                return;
            }
            case "/edit_description": {
                try {
                    updateUserService.setEditDescriptionContext(message.getUser().getId());
                } catch (SQLException e) {
                    message.getBotFrom().sendTextMessage(
                            message.getUserIdOnPlatform(),
                            "Проблемы с базой данных" +
                                    e.getMessage()
                    );
                }
                message.getBotFrom().sendTextMessage(
                        message.getUserIdOnPlatform(),
                        UpdateUserServiceConfig.EDIT_DESCRIPTION_MESSAGE.getStr()
                );
                return;
            }

            case "/cancel": {
                String text = updateUserService.endSession(message.getUser().getId());
                message.getBotFrom().sendTextMessage(
                        message.getUserIdOnPlatform(),
                        text
                );
                return;
            }

            case "/help": {
                message.getBotFrom().sendTextMessage(
                        message.getUserIdOnPlatform(),
                        UpdateUserServiceConfig.START_MESSAGE.getStr()
                );
                return;
            }
        }

        switch(message.getUserContext().getStateNum()) {
            case 1: {
                try {
                    updateUserService.updateUsername(message.getText(), message.getUser());
                    updateUserService.resetEditContext(message.getUser().getId());
                } catch (SQLException e) {
                    message.getBotFrom().sendTextMessage(
                            message.getUserIdOnPlatform(),
                            "Проблемы с базой данных" +
                                    e.getMessage()
                    );
                }

                message.getBotFrom().sendTextMessage(message.getUserIdOnPlatform(),
                        UpdateUserServiceConfig.USERNAME_UPDATED_SUCCESFULLY.getStr());
                return;
            }
            case 2: {
                try {
                    updateUserService.updateDescription(message.getText(), message.getUser());
                    updateUserService.resetEditContext(message.getUser().getId());
                } catch (SQLException e) {
                    message.getBotFrom().sendTextMessage(
                            message.getUserIdOnPlatform(),
                            "Проблемы с базой данных" +
                                    e.getMessage()
                    );
                }
                message.getBotFrom().sendTextMessage(message.getUserIdOnPlatform(),
                        UpdateUserServiceConfig.DESCRIPTION_UPDATED_SUCCESFULLY.getStr());
                return;
            }
        }

        message.getBotFrom().sendTextMessage(
                message.getUserIdOnPlatform(),
                "Введите команду."
        );
    }
}
