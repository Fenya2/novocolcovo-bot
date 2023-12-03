package core.service_handlers.handlers;

import config.services.EditUserServiceConfig;
import db.DBException;
import models.Message;
import core.service_handlers.services.EditUserService;

import java.sql.SQLException;

/** Обработчик контекста изменения пользователя. работае с EDIT_ */
public class HandlerEditUserService {

    /** @see EditUserService*/
    private final EditUserService editUserService;

    /** Конструктор {@link HandlerEditUserService}*/
    public HandlerEditUserService(EditUserService updateUserService) {
        this.editUserService = updateUserService;
    }

    /**
     * Обрабатывает сообщение пользователя, когда тот
     * находится в контексте обновления аккаунта пользователя.
     * @return 1, если текст сообщения корректная команда,
     * 2, если введенный текст привязан к контексту одной из команд {@link EditUserService}
     * 3, если введенный тест не является ни тем, ни другим.
     */
    public int handle(Message msg) {
        switch (msg.getText()) {
            case "/show_profile": {
                msg.getBotFrom().sendTextMessage(
                        msg.getUserIdOnPlatform(),
                        editUserService.generateProfileMessage(msg.getUser().getId())
                );
                return 1;
            }
            case "/edit_username": {
                try {
                    editUserService.setEditUsernameContext(msg.getUser().getId());
                    String message = EditUserServiceConfig.EDIT_USER_MESSAGE.getStr();
                    msg.getBotFrom().sendTextMessage(msg.getUserIdOnPlatform(),message);
                } catch (SQLException e) {
                    msg.getBotFrom().sendTextMessage(
                            msg.getUserIdOnPlatform(),
                            "Проблемы с базой данных" +
                                    e.getMessage()
                    );
                }
                return 1;
            }
            case "/edit_description": {
                try {
                    editUserService.setEditDescriptionContext(msg.getUser().getId());
                    msg.getBotFrom().sendTextMessage(
                            msg.getUserIdOnPlatform(),
                            EditUserServiceConfig.EDIT_DESCRIPTION_MESSAGE.getStr()
                    );
                } catch (SQLException e) {
                    msg.getBotFrom().sendTextMessage(
                            msg.getUserIdOnPlatform(),
                            "Проблемы с базой данных" +
                                    e.getMessage()
                    );
                }
                return 1;
            }

            case "/edit_login": {
                try {
                    editUserService.setEditLoginContext(msg.getUser().getId());
                    msg.getBotFrom().sendTextMessage(
                            msg.getUserIdOnPlatform(),
                            EditUserServiceConfig.EDIT_LOGIN_MESSAGE.getStr()
                    );
                } catch (SQLException e) {
                    msg.getBotFrom().sendTextMessage(
                            msg.getUserIdOnPlatform(),
                            "Проблемы с базой данных" +
                                    e.getMessage()
                    );
                }
                return 1;
            }

            case "/done": {
                String text = editUserService.endSession(msg.getUser().getId());
                msg.getBotFrom().sendTextMessage(
                        msg.getUserIdOnPlatform(),
                        text
                );
                msg.getBotFrom().sendMainMenu(
                        msg.getUserIdOnPlatform(),
                        "Вы попали в главное меню. Выберите действие"
                );
                return 1;
            }

            case "/help": {
                msg.getBotFrom().sendTextMessage(
                        msg.getUserIdOnPlatform(),
                        EditUserServiceConfig.HELP_MESSAGE.getStr()
                );
                return 1;
            }
        }

        switch(msg.getUserContext().getStateNum()) {
            case 1: {
                try {
                    editUserService.updateUsername(msg.getText(), msg.getUser());
                    editUserService.resetEditContext(msg.getUser().getId());
                    msg.getBotFrom().sendTextMessage(
                            msg.getUserIdOnPlatform(),
                            EditUserServiceConfig.USERNAME_UPDATED_SUCCESFULLY.getStr()
                    );
                    return 2;
                } catch (SQLException | DBException e) {
                    msg.getBotFrom().sendTextMessage(
                            msg.getUserIdOnPlatform(),
                            "Проблемы с базой данных" +
                                    e.getMessage()
                    );
                }
            }
            case 2: {
                try {
                    editUserService.updateDescription(msg.getText(), msg.getUser());
                    editUserService.resetEditContext(msg.getUser().getId());
                    msg.getBotFrom().sendTextMessage(
                            msg.getUserIdOnPlatform(),
                            EditUserServiceConfig.DESCRIPTION_UPDATED_SUCCESFULLY.getStr()
                    );
                    return 2;
                } catch (SQLException | DBException e) {
                    msg.getBotFrom().sendTextMessage(
                            msg.getUserIdOnPlatform(),
                            "Проблемы с базой данных" +
                                    e.getMessage()
                    );
                }
            }
            case 3: {
                try {
                    if(editUserService.updateLogin(msg.getText(), msg.getUser())) {
                        msg.getBotFrom().sendTextMessage(
                                msg.getUserIdOnPlatform(),
                                EditUserServiceConfig.LOGIN_UPDATED_SUCCESFULLY.getStr()
                        );
                        editUserService.resetEditContext(msg.getUser().getId());
                        return 2;
                    }
                    msg.getBotFrom().sendTextMessage(
                            msg.getUserIdOnPlatform(),
                            EditUserServiceConfig.LOGIN_EXIST_ERROR.getStr()
                    );
                    return 2;
                } catch (SQLException | DBException e) {
                    msg.getBotFrom().sendTextMessage(
                            msg.getUserIdOnPlatform(),
                            "Проблемы с базой данных" +
                                    e.getMessage()
                    );
                }
            }
        }

        msg.getBotFrom().sendTextMessage(
                msg.getUserIdOnPlatform(),
                "Неопределенное поведение. Введите команду /help для справки."
        );
        return 3;
    }
}
