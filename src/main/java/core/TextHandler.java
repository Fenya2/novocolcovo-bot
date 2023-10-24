package core;

import core.service.OrderService;
import db.LoggedUsersRepository;
import db.UserContextRepository;
import models.Message;
import models.User;
import models.UserContext;

import java.sql.SQLException;


public class TextHandler {
    /**
     * сервис для проверки контекста
     */
    private final LoggedUsersRepository loggedUsersRepository;
    private final UserContextRepository userContextRepository;

    private final OrderService orderService;
    public TextHandler( LoggedUsersRepository loggedUsersRepository, UserContextRepository userContextRepository, OrderService orderService) {
        this.loggedUsersRepository = loggedUsersRepository;
        this.userContextRepository = userContextRepository;
        this.orderService = orderService;
    }

    /**
     * Обработчик текста проверяет на наличие контекста (например конеткст регистрации или создания заказа)
     * у сообщения
     * @param msg
     * @return возвращает работу работу сервиса связанного с контекстом
     * или в случае его отсутствия соответствующее сообщение
     */
    public String handle(Message msg) {
        try {
            String platform = msg.getPlatform();
            String userIdOnPlatform = msg.getUserIdOnPlatform();

            User userWithId = loggedUsersRepository.getUserByPlatformAndIdOnPlatform(platform,userIdOnPlatform);
            if(userWithId == null)
                return "напишите /start";

            UserContext userContext = userContextRepository.getUserContext(userWithId.getId());
            if(userContext == null)
                return "я вас не понимаю";

            switch(userContext.getState()) {
                case "create_order" ->{return orderService.continueCreateOrder(userWithId.getId(), msg.getText());}
                case "update_order" ->{return orderService.continueUpdateOrder(userWithId.getId(), msg.getText());}
                case "cancel_order" ->{return orderService.continueСancelOrder(userWithId.getId(), msg.getText());}
                default -> {return "Извините, я вас не понял. Вызовите команду /help";}
            }
        }
        catch (SQLException e) {
            return "Пользователь не найден";
        }
    }
}