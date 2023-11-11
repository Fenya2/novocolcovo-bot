package core.service_handlers.services;

import core.MessageSender;
import db.LoggedUsersRepository;
import db.UserContextRepository;
import models.Message;
import models.User;
import models.UserContext;
import models.UserState;

import java.sql.SQLException;

/** Сервис для работы с контекстом {@link models.UserState#LOGGED LOGGED}*/
public class LoginService {

    /** @see LoggedUsersRepository*/
    private final LoggedUsersRepository loggedUsersRepository;

    /** @see UserContextRepository*/
    private final UserContextRepository userContextRepository;

    public LoginService(LoggedUsersRepository loggedUsersRepository, UserContextRepository userContextRepository) {
        this.loggedUsersRepository = loggedUsersRepository;
        this.userContextRepository = userContextRepository;
    }

    public String start(Message msg) {
        try {
            User user = loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                    msg.getPlatform(),
                    msg.getUserIdOnPlatform()
            );
            //TODO нужен генератор чисел
            String code = "1000";
            userContextRepository.updateUserContext(
                    user.getId(),
                    new UserContext(UserState.LOGGED,Integer.parseInt(code))
            );
            return "Ваш код подтверждения: "+ code;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String continueSession(Message msg) {
        long id = msg.getUser().getId();
        String text = msg.getText();
        try {
            UserContext userContext = userContextRepository.getUserContext(id);
            if (text.equals(Integer.toString(userContext.getStateNum()))){
                loggedUsersRepository.linkUserIdAndUserPlatform(id,msg.getPlatform(),msg.getUserIdOnPlatform());
                return "Поздравляю, ты вошел";
            }
            return "Неверный код. Попробуй еще раз";

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
