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
     * репозиторий для опознания пользователя в системе
     */
    private final LoggedUsersRepository loggedUsersRepository;

    /**
     * репозиторий для работы с таблицей контекста пользоваталей
     */
    private final UserContextRepository userContextRepository;

    /**
     * сервис для работы с заказами
     */
    private final OrderService orderService;

    /**
     * @param loggedUsersRepository репозиторий для опознания пользователя в системе
     * @param userContextRepository репозиторий для работы с таблицей контекста пользоваталей
     * @param orderService сервис для работы с заказами
     */
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
                case "edit_order" ->{return orderService.continueEditOrder(userWithId.getId(), msg.getText());}
                case "cancel_order" ->{return orderService.continueCancelOrder(userWithId.getId(), msg.getText());}
            }
        }
        catch (SQLException e) {
            return " Пользователь не найден";
        }
        return null;
    }
}