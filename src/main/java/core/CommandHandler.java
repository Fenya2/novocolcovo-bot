package core;

import core.service.OrderService;
import db.LoggedUsersRepository;
import db.OrderRepository;
import db.UserContextRepository;
import db.UserRepository;
import models.Message;
import models.User;
import models.UserContext;

import java.sql.SQLException;

public class CommandHandler {
    /**
     * содержит информацию о пользователях.
     */
    private final UserRepository userRepository;
    /**
     * Позволяет опознать пользователя в системе
     * содержащит информацию о платформе, id  в этой платформе и id в системе
     */
    private final LoggedUsersRepository loggedUsersRepository;
    private final UserContextRepository userContextRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    public CommandHandler(UserRepository userRepository, LoggedUsersRepository loggedUsersRepository, UserContextRepository userContextRepository, OrderRepository orderRepository, OrderService orderService){
        this.userRepository = userRepository;
        this.loggedUsersRepository = loggedUsersRepository;
        this.userContextRepository = userContextRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    /**
     * Обрабатыевает команды и передает в соответствующий сервис
     * @param msg
     * @return возвращает работу сервиса
     */
    public String handle(Message msg){
        try {
            String text = msg.getText();
            switch (text) {
                case "/help" -> {return """
                        /create_order - создать заказ
                        /update_order - изменить заказ
                        /cancel_order - удалить заказ
                        /view_list_order - посмотреть список заказов
                        """;}
                case "/start" -> {return start(msg);}
            }

            User user = loggedUsersRepository.getUserByPlatformAndIdOnPlatform(msg.getPlatform(), msg.getUserIdOnPlatform());
            if (user == null)
                return "Напишите /start";

            UserContext userContext = userContextRepository.getUserContext(user.getId());
            if(userContext!=null)
                return "Сейчас команды не доступны";
            long idUser = user.getId();
            switch (text){
                case "/create_order" -> {return orderService.startCreateOrder(idUser);}
                case "/update_order" -> {return orderService.startUpdateOrder(idUser);}
                case "/cancel_order" -> {return orderService.startСancelOrder(idUser);}
                case "/view_list_order" -> {return orderService.viewListOrder(idUser);}
                default -> {return "Извините я вас не понимаю. Напишите /help.";}
            }
        } catch (SQLException e){
            return "Что-то пошло не так";
        }


    }

    /**
     * Проверяет наличие пользователя в системе, если нету то добавляет в таблицы User и LoggedUsers
     * @param msg
     * @return сообщение с приветствием
     * В случае ошибки возвращает сообщение об ошибке
     */
    public String start(Message msg) {
        //TODO реализовать в отдельном сервисе
        String platform = msg.getPlatform();
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
