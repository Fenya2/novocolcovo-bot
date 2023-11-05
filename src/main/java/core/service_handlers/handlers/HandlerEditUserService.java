package core.service_handlers.handlers;

import config.services.EditUserServiceConfig;
import models.Message;
import core.service_handlers.services.EditUserService;

import java.sql.SQLException;
/** Обработчик контекста изменения пользователя. */
public class HandlerEditUserService {

    /** @see EditUserService*/
    private final EditUserService editUserService;

    /** Конструктор {@link HandlerEditUserService}*/
    public HandlerEditUserService(EditUserService updateUserService) {
        this.editUserService = updateUserService;
    }

    /**
     * Обрабатывает сообщение пользователя, когда тот находится в контексте обновления аккаунта
     * пользователя.
     * @param msg
     */
    public void handle(Message msg) {
        switch (msg.getText()) {
            case "/edit_username": {
                try {
                    editUserService.setEditUsernameContext(msg.getUser().getId());
                } catch (SQLException e) {
                    msg.getBotFrom().sendTextMessage(
                            msg.getUserIdOnPlatform(),
                            "Проблемы с базой данных" +
                                    e.getMessage()
                    );
                }
                String message = EditUserServiceConfig.EDIT_USER_MESSAGE.getStr();
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(),message);
                return;
            }
            case "/edit_description": {
                try {
                    editUserService.setEditDescriptionContext(msg.getUser().getId());
                } catch (SQLException e) {
                    msg.getBotFrom().sendTextMessage(
                            msg.getUserIdOnPlatform(),
                            "Проблемы с базой данных" +
                                    e.getMessage()
                    );
                }
                msg.getBotFrom().sendTextMessage(
                        msg.getUserIdOnPlatform(),
                        EditUserServiceConfig.EDIT_DESCRIPTION_MESSAGE.getStr()
                );
                return;
            }

            case "/cancel": {
                String text = editUserService.endSession(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(
                        msg.getUserIdOnPlatform(),
                        text
                );
                return;
            }

            case "/help": {
                msg.getBotFrom().sendTextMessage(
                        msg.getUserIdOnPlatform(),
                        EditUserServiceConfig.START_MESSAGE.getStr()
                );
                return;
            }
        }

        switch(msg.getUserContext().getStateNum()) {
            case 1: {
                try {
                    editUserService.updateUsername(msg.getText(), msg.getUser());
                    editUserService.resetEditContext(msg.getUser().getId());
                } catch (SQLException e) {
                    msg.getBotFrom().sendTextMessage(
                            msg.getUserIdOnPlatform(),
                            "Проблемы с базой данных" +
                                    e.getMessage()
                    );
                }

                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(),
                        EditUserServiceConfig.USERNAME_UPDATED_SUCCESFULLY.getStr());
                return;
            }
            case 2: {
                try {
                    editUserService.updateDescription(msg.getText(), msg.getUser());
                    editUserService.resetEditContext(msg.getUser().getId());
                } catch (SQLException e) {
                    msg.getBotFrom().sendTextMessage(
                            msg.getUserIdOnPlatform(),
                            "Проблемы с базой данных" +
                                    e.getMessage()
                    );
                }
                msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(),
                        EditUserServiceConfig.DESCRIPTION_UPDATED_SUCCESFULLY.getStr());
                return;
            }
        }

        msg.getBotFrom().sendTextMessage(
                msg.getUserIdOnPlatform(),
                "Введите команду."
        );
    }
}
