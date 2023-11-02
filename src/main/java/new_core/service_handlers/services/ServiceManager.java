package new_core.service_handlers.services;

import db.LoggedUsersRepository;
import db.OrderRepository;
import db.UserContextRepository;
import db.UserRepository;
import models.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

public class ServiceManager {
    private  final LoggedUsersRepository loggedUsersRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final UserContextRepository userContextRepository;

    public ServiceManager(LoggedUsersRepository loggedUsersRepository,
                          OrderRepository orderRepository,
                          UserRepository userRepository,
                          UserContextRepository userContextRepository) {
        this.loggedUsersRepository = loggedUsersRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.userContextRepository = userContextRepository;
    }


    public String start(Message msg) {
        //TODO реализовать в отдельном сервисе
        Platform platform = msg.getPlatform();
        String userIdOnPlatform = msg.getUserIdOnPlatform();
        try {
            if (loggedUsersRepository.getUserByPlatformAndIdOnPlatform(platform,userIdOnPlatform) == null) {
                User user = new User(0,"User","Я есть user");
                User userWithID = userRepository.save(user);
                loggedUsersRepository.linkUserIdAndUserPlatform(userWithID.getId(),platform,userIdOnPlatform);
            }
            return "Привет. Напишите /help";
        } catch (Exception e) {
            return "Что-то пошло не так";
        }
    }
    public String startCreateOrder(long idUser) {
        try {
            Order order = orderRepository.save(new Order(idUser));
            orderRepository.updateOrderStatus(order.getId(), OrderStatus.UPDATING);
            UserContext userContext = new UserContext(UserState.ORDER_CREATING, 0);
            userContextRepository.saveUserContext(idUser, userContext);
            return "Введите список продуктов";
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так";
        }
    }
    public String startEditOrder(long idUser) {
        try {
            ArrayList<Order> listAllOrder = orderRepository.getAll();
            StringBuilder allOrderUser = new StringBuilder();
            for (Order s : listAllOrder) {
                if (s.getCreatorId() == idUser)
                    allOrderUser.append(Long.toString(s.getId()).concat(": ")
                            .concat(s.getDescription()).concat("\n"));
            }
            if (allOrderUser.isEmpty())
                return "у вас нет ни одного заказа";
            //TODO
            UserContext userContext = new UserContext(UserState.ORDER_EDITING, 0);
            userContextRepository.saveUserContext(idUser, userContext);
            return "Какой заказ вы хотите обновить.?\n"
                    .concat(allOrderUser.toString());
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так";
        }
    }
    public String startCancelOrder(long idUser) {
        try {
            ArrayList<Order> listAllOrder = orderRepository.getAll();
            StringBuilder allOrderUser = new StringBuilder();
            for (Order s : listAllOrder) {
                if (s.getCreatorId() == idUser) {
                    allOrderUser.append(Long.toString(s.getId()).concat(": ")
                            .concat(s.getDescription().concat("\n")));
                }
            }
            if (allOrderUser.isEmpty())
                return "у вас нет ни одного заказа";

            UserContext userContext = new UserContext(UserState.ORDER_CANCELING, 0);
            userContextRepository.saveUserContext(idUser, userContext);
            return "Какой заказ вы хотите удалить.?\n"
                    .concat(allOrderUser.toString());
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так";
        }
    }
    public String showOrder(long idUser) {
        try {
            ArrayList<Order> listAllOrder = orderRepository.getAll();
            StringBuilder allOrderUser = new StringBuilder();
            for (Order s : listAllOrder) {
                if (s.getCreatorId() == idUser)
                    allOrderUser.append(
                            Long.toString(s.getId()).concat(": ")
                                    .concat(s.getDescription()).concat("\n")
                    );
            }
            if (allOrderUser.isEmpty())
                return "у вас нет ни одного заказа";
            return allOrderUser.toString();
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так";
        }
    }
}
