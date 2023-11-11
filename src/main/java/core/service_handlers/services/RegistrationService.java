package core.service_handlers.services;

import db.OrderRepository;
import db.UserContextRepository;
import db.UserRepository;
import models.User;
import models.UserContext;
import java.sql.SQLException;

/** Сервис для работы с контекстом {@link models.UserState#REGISTRATION REGISTRATION}*/
public class RegistrationService {

    /** @see OrderRepository */
    private final UserRepository userRepository;

    /** @see UserContextRepository */
    private final UserContextRepository userContextRepository;

    /** Конструктор {@link CreateOrderService}*/
    public RegistrationService(UserRepository userRepository, UserContextRepository userContextRepository) {
        this.userRepository = userRepository;
        this.userContextRepository = userContextRepository;
    }

    /**
     * Измеряет соответствующее поле(либо имя, либо описание) на новое <br>
     * в последнем {@link UserContext#getStateNum() состоянии контекста} удаляет контекст у пользователя
     */
    public String continueSession(long userId, String text) {
        User user;
        UserContext userContext;
        try {
            user = userRepository.getById(userId);
            userContext = userContextRepository.getUserContext(userId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        switch (userContext.getStateNum()){
            case 0->{
                user.setName(text);
                userContext.incrementStateNum();
                try {
                    userContextRepository.updateUserContext(userId,userContext);
                    userRepository.updateUser(user);
                } catch (SQLException e) {
                    return "что-то пошло не так" + e.getMessage();
                }
                return "Расскажи о себе";
            }
            case 1->{
                user.setDescription(text);
                try {
                    userContextRepository.updateUserContext(userId,new UserContext());
                    userRepository.updateUser(user);
                } catch (SQLException e) {
                    return "что-то пошло не так" + e.getMessage();
                }
                return "Поздравляю, регистрация завершена!";
            }
            default -> {
                return "Выход за пределы контекста";
            }
        }
    }
}
