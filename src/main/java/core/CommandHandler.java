package core;

import core.service.OrderService;
import db.LoggedUsersRepository;
import db.UserContextRepository;
import db.UserRepository;
import models.Message;
import models.Platform;
import models.User;
import models.UserContext;

import java.sql.SQLException;

public class CommandHandler {
    /** Cодержит информацию о пользователях. */
    private final UserRepository userRepository;
    /** Позволяет опознать пользователя в системе. */
    private final LoggedUsersRepository loggedUsersRepository;
    /** Содержит информацио о контексте пользователя. */
    private final UserContextRepository userContextRepository;
    /** Содержит информацию о заказах. */
    private final OrderService orderService;

    /**
     * @param userRepository содержит информацию о пользователях.
     * @param loggedUsersRepository Позволяет опознать пользователя в системе.
     * @param userContextRepository Содержит информацио о контексте пользователя.
     * @param orderService Содержит информацию о заказах.
     */
    public CommandHandler(UserRepository userRepository, LoggedUsersRepository loggedUsersRepository, UserContextRepository userContextRepository, OrderService orderService){
        this.userRepository = userRepository;
        this.loggedUsersRepository = loggedUsersRepository;
        this.userContextRepository = userContextRepository;
        this.orderService = orderService;
    }

    /**
     * Обрабатыевает команды и передает в соответствующий сервис для ее выполнения.
     * @param msg
     * @return возвращает работу сервиса
     */
    public String handle(Message msg){
        try {
            String text = msg.getText();
            switch (text) {
                case "/start" -> {return start(msg);}
                case "/help" -> {return """
                        /create_order - создать заказ
                        /edit_order - изменить заказ
                        /cancel_order - удалить заказ
                        /show_order - посмотреть список заказов
                        """;}
            }

            User user = loggedUsersRepository.getUserByPlatformAndIdOnPlatform(msg.getPlatform(),
                    msg.getUserIdOnPlatform());
            if (user == null)
                return "Напишите /start";

            UserContext userContext = userContextRepository.getUserContext(user.getId());
            if(userContext!=null)
                return "Сейчас команды не доступны";
            long idUser = user.getId();
            switch (text){
                case "/create_order" -> {return orderService.startCreateOrder(idUser);}
                case "/edit_order" -> {return orderService.startEditOrder(idUser);}
                case "/cancel_order" -> {return orderService.startCancelOrder(idUser);}
                case "/show_order" -> {return orderService.showOrder(idUser);}
                default -> {return "Извините я вас не понимаю. Напишите /help.";}
            }
        } catch (SQLException e){
            return "Что-то пошло не так";
        }
    }

    /**
     * Проверяет наличие пользователя в системе, если нету то добавляет в таблицы User и LoggedUsers
     * @param msg сообщение от {@link MessageHandler}.
     * @return сообщение с приветствием.
     * В случае ошибки возвращает сообщение об ошибке.
     */
    public String start(Message msg) {
        //TODO реализовать в отдельном сервисе
        Platform platform = msg.getPlatform();
        String userIdOnPlatform = msg.getUserIdOnPlatform();
        User user = new User(0,"User","Я есть user");
        try {
            if (loggedUsersRepository.getUserByPlatformAndIdOnPlatform(platform,userIdOnPlatform) == null) {
                User userWithID = userRepository.save(user);
                loggedUsersRepository.linkUserIdAndUserPlatform(userWithID.getId(),platform,userIdOnPlatform);
            }
            return "Привет. Напишите /help";
        } catch (Exception e) {
            return "Что-то пошло не так";
        }
    }
}
