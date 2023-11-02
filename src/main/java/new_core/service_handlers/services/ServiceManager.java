package new_core.service_handlers.services;

import core.MessageHandler;
import core.service.OrderService;
import db.LoggedUsersRepository;
import db.OrderRepository;
import db.UserContextRepository;
import db.UserRepository;
import models.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

public class ServiceManager {
    /**
     * Cодержит информацию о пользователях.
     */
    private final UserRepository userRepository;
    /**
     * Позволяет опознать пользователя в системе.
     */
    private final LoggedUsersRepository loggedUsersRepository;
    /**
     * Содержит информацио о контексте пользователя.
     */
    private final UserContextRepository userContextRepository;
    /**
     * Содержит информацию о заказах.
     */
    private final OrderRepository orderRepository;

    /**
     * @param userRepository        содержит информацию о пользователях.
     * @param loggedUsersRepository Позволяет опознать пользователя в системе.
     * @param userContextRepository Содержит информацио о контексте пользователя.
     * @param orderRepository       Содержит информацию о заказах.
     */
    public ServiceManager(LoggedUsersRepository loggedUsersRepository,
                          OrderRepository orderRepository,
                          UserRepository userRepository,
                          UserContextRepository userContextRepository) {
        this.loggedUsersRepository = loggedUsersRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.userContextRepository = userContextRepository;
    }

    /**
     * Проверяет наличие пользователя в системе, если нету то добавляет в таблицы User и LoggedUsers
     *
     * @param msg сообщение от {@link MessageHandler}.
     * @return сообщение с приветствием.
     * В случае ошибки возвращает сообщение об ошибке.
     */
    public String start(Message msg) {
        Platform platform = msg.getPlatform();
        String userIdOnPlatform = msg.getUserIdOnPlatform();
        try {
            if (loggedUsersRepository.getUserByPlatformAndIdOnPlatform(platform, userIdOnPlatform) == null) {
                User user = new User(0, "User", "Я есть user");
                User userWithID = userRepository.save(user);
                loggedUsersRepository.linkUserIdAndUserPlatform(userWithID.getId(), platform, userIdOnPlatform);
                userContextRepository.saveUserContext(userWithID.getId(), new UserContext());
            }
            return "Привет. Напишите /help";
        } catch (Exception e) {
            return "Что-то пошло не так";
        }
    }

    /**
     * Начало создания заказа. <br>
     * Создает заказ и добавляет в таблицу order, обновляет статус у заказа
     * и добавляет контекст в таблицу userContext.
     *
     * @param idUser идентификатор пользователя.
     * @return Сообщение для дальнейшего создания заказа.
     */
    public String startCreateOrder(long idUser) {
        try {
            Order order = orderRepository.save(new Order(idUser));
            orderRepository.updateOrderStatus(order.getId(), OrderStatus.UPDATING);
            UserContext userContext = new UserContext(UserState.ORDER_CREATING, 0);
            userContextRepository.updateUserContext(idUser, userContext);
            return "Введите список продуктов";
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так";
        }
    }

    /**
     * Начало обновления заказа.  <br>
     * Получает список всех заказов и из них выбирает заказы пользователя
     * добавляет контекст пользователя в бд
     *
     * @param idUser order
     * @return Выводит список всех заказов пользователя, что бы пользователь мог выбрать какой заказ обновить
     */
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
            userContextRepository.updateUserContext(idUser, userContext);
            return "Какой заказ вы хотите обновить.?\n"
                    .concat(allOrderUser.toString());
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так";
        }
    }


    /**
     * Начало удаления заказа.  <br>
     * Получает список всех заказов и из них выбирает заказы пользователя
     *
     * @param idUser long
     * @return Выводит список всех заказов пользователя, что бы пользователь мог выбрать какой заказ удалить
     */
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
            userContextRepository.updateUserContext(idUser, userContext);
            return "Какой заказ вы хотите удалить.?\n"
                    .concat(allOrderUser.toString());
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так";
        }
    }


    /**
     * @param idUser long
     * @return Возвращает список все заказов пользователя с их описанием
     */
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
