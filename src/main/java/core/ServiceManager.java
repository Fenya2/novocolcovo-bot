package core;

import config.services.EditUserServiceConfig;
import db.LoggedUsersRepository;
import db.OrderRepository;
import db.UserContextRepository;
import db.UserRepository;
import models.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;


/** Главный сервис. Работает c контекстом {@link UserState#NO_STATE NO_STATE}*/
public class ServiceManager {

    /** @see UserRepository*/
    private final UserRepository userRepository;

    /** @see LoggedUsersRepository*/
    private final LoggedUsersRepository loggedUsersRepository;

    /** @see UserContextRepository*/
    private final UserContextRepository userContextRepository;

    /** @see OrderRepository*/
    private final OrderRepository orderRepository;

    /**Конструктор {@link ServiceManager ServiceManager} */
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
     * Проверяет наличие пользователя в системе, если нет то добавляет в таблицы User и LoggedUsers
     *
     * @param msg сообщение от {@link core.MessageHandler}.
     * @return сообщение с приветствием.
     * В случае ошибки возвращает сообщение об ошибке.
     */
    public String start(Message msg) {
        Platform platform = msg.getPlatform();
        String userIdOnPlatform = msg.getUserIdOnPlatform();
        try {
            User user = new User(0, "User", "Я есть user");
            if (loggedUsersRepository.getUserByPlatformAndIdOnPlatform(platform, userIdOnPlatform) == null) {
                User userWithID = userRepository.save(user);
                loggedUsersRepository.linkUserIdAndUserPlatform(userWithID.getId(), platform, userIdOnPlatform);
                userContextRepository.saveUserContext(userWithID.getId(), new UserContext(UserState.NO_STATE));
            }
            return "Привет \uD83D\uDC4B Команда /help поможет тебе разобраться, что тут происходит";
        } catch (Exception e) {
            return "Что-то пошло не так"+ e.getMessage();
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
            UserContext userContext = new UserContext(UserState.ORDER_CREATING);
            userContextRepository.updateUserContext(idUser, userContext);
            return "Введите список продуктов";
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так"+ e.getMessage();
        }
    }

    /**
     * Начало обновления заказа.  <br>
     * Получает список всех заказов и из них выбирает заказы пользователя
     * и добавляет контекст пользователя в бд
     *
     * @param idUser order
     * @return Выводит список всех заказов пользователя, что бы пользователь мог выбрать какой заказ обновить
     */
    public String startEditOrder(long idUser) {
        try {
            String allOrderUser = showOrder(idUser);
            if(allOrderUser.equals("У вас нет ни одного заказа"))
                return "У вас нет ни одного заказа";
            UserContext userContext = new UserContext(UserState.ORDER_EDITING);
            userContextRepository.updateUserContext(idUser, userContext);
            return "Какой заказ вы хотите обновить.?\n" + allOrderUser;
        } catch (SQLException e) {
            return "что-то пошло не так"+ e.getMessage();
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
            String allOrderUser = showOrder(idUser);
            if(allOrderUser.equals("У вас нет ни одного заказа"))
                return "У вас нет ни одного заказа";

            UserContext userContext = new UserContext(UserState.ORDER_CANCELING);
            userContextRepository.updateUserContext(idUser, userContext);
            return "Какой заказ вы хотите удалить.?\n" + allOrderUser;
        } catch (SQLException e) {
            return "что-то пошло не так" + e.getMessage();
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
                return "У вас нет ни одного заказа";
            return allOrderUser.toString();
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так" +e.getMessage();
        }
    }

    /**
     * Начинает процедуру изменения описания пользователя в системе, меняет контекст пользователя на
     * {@link UserState EDIT_USER}
     * @param userId идентификатор пользователя, с которым нужно начать сессию.
     * @return приветственное сообщение сервиса. Содержащее команды, с помощью которых можно
     * изменить описание пользователя, его имя.
     */

    public String startEditUserService(long userId){
        UserContext userContext = new UserContext(UserState.EDIT_USER);
        try {
            userContextRepository.updateUserContext(userId, userContext);
        } catch (SQLException e) {
            return "Ошибка при обращении к базе данных." + e.getMessage();
        }
        return EditUserServiceConfig.HELP_MESSAGE.getStr();
    }

     /**
      * Выбирает из списка всех заказов заказы с состоянием {@link OrderStatus#PENDING PENDING}
      * и такие что курьер c userId не был равен {@link Order#getCreatorId()}  заказчику} и выводит их
      * @param userId пользователя, который хочет посмотреть список заказов
     */
    public String showPendingOrders(long userId) {
        try {
            ArrayList<Order> listAllOrder = orderRepository.getAll();
            StringBuilder allOrderUser = new StringBuilder();
            UserContext client;
            for (Order s: listAllOrder){
                client = userContextRepository.getUserContext(s.getId());
                if(userId != s.getCreatorId() && s.getStatus().equals(OrderStatus.PENDING)
                        && client.getState() == UserState.NO_STATE){
                    allOrderUser.append(
                            Long.toString(s.getId()).concat(": ")
                                    .concat(s.getDescription()).concat("\n")
                    );
                }
            }
            if (allOrderUser.isEmpty())
                return "У вас нет ни одного заказа";
            return allOrderUser.toString();

        } catch (SQLException | ParseException e) {
            return "что-то пошло не так" + e.getMessage();
        }
    }

    /**
     * @param userId Добавляет пользователя с этим userId в контекст {@link UserState#ORDER_ACCEPTING_COURIER ORDER_ACCEPTING}
     * @return Выводит сообщение с просьбой ввести курьера userId заказа, который он хочется принять 
     */
    public String startAcceptOrder(long userId){
        if (showPendingOrders(userId).equals("У вас нет ни одного заказа"))
            return "У вас нет ни одного заказа";
        UserContext userContext = new UserContext(UserState.ORDER_ACCEPTING_COURIER);
        try {
            userContextRepository.updateUserContext(userId, userContext);
        } catch (SQLException e) {
            return "Ошибка при обращении к базе данных." + e.getMessage();
        }

        return "Введите заказ который хотите принять";
    }

    /**
     * Выбирает из списка всех заказов заказы с состоянием {@link OrderStatus#RUNNING RUNNING} 
     * и такие что userId курьера = {@link Order#getCourierId()}  courierId} и выводит их
     * @param userId курьер, который хочет посмотреть свои заказы
     */
    public String showAcceptOrder(long userId) {
        try {
            ArrayList<Order> listAllOrder = orderRepository.getAll();
            StringBuilder allOrderUser = new StringBuilder();
            UserContext client;
            for (Order s: listAllOrder){
                client = userContextRepository.getUserContext(s.getId());
                if(userId == s.getCourierId() && s.getStatus().equals(OrderStatus.RUNNING)
                        && client.getState() == UserState.NO_STATE){
                    allOrderUser.append(
                            Long.toString(s.getId()).concat(": ")
                                    .concat(s.getDescription()).concat("\n")
                    );
                }
            }
            if (allOrderUser.isEmpty())
                return "У вас нет ни одного заказа";
            return allOrderUser.toString();

        } catch (SQLException | ParseException e) {
            return "что-то пошло не так" + e.getMessage();
        }
    }

    /**
     * @param userId Добавляет пользователя с этим userId в контекст {@link UserState#ORDER_CLOSING_COURIER ORDER_CLOSING_COURIER}
     * @return Выводит сообщение с просьбой ввести курьера userId заказа, который он хочется удалить
     */
    public String startCloseOrder(long userId) {
        if (showAcceptOrder(userId).equals("У вас нет ни одного заказа"))
            return "У вас нет ни одного заказа";
        UserContext userContext = new UserContext(UserState.ORDER_CLOSING_COURIER);
        try {
            userContextRepository.updateUserContext(userId, userContext);
        } catch (SQLException e) {
            return "Ошибка при обращении к базе данных." + e.getMessage();
        }


        return "Введите заказ который хотите завершить";
    }
}
