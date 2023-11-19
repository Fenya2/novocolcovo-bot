package core;

import config.BotMessages;
import config.services.EditUserServiceConfig;
import core.service_handlers.services.LoginService;
import db.*;
import models.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;


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

    /** Сервис авторизации. Не работает с контекстом, поэтому здесь */
    private final LoginService loginService;

    /**Конструктор {@link ServiceManager ServiceManager} */
    public ServiceManager(LoggedUsersRepository loggedUsersRepository,
                          OrderRepository orderRepository,
                          UserRepository userRepository,
                          UserContextRepository userContextRepository,
                          LoginService loginService
    ) {
        this.loggedUsersRepository = loggedUsersRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.userContextRepository = userContextRepository;

        this.loginService = loginService;
    }

    /**
     * Проверяет наличие пользователя в системе, если нет то добавляет в таблицы User и LoggedUsers
     * и таблицу контекста, меняет контекст пользователя на EDIT_USER, чтобы он сразу мог начать
     * настраивать аккаунт. Если пользователь пишет команду из аккаунта, сообщает об этом.
     *
     * @param msg сообщение от {@link core.MessageHandler}.
     * @return сообщение с приветствием.
     * В случае ошибки возвращает сообщение об ошибке.
     */
    public String register(Message msg) {

        if(msg.getUser() != null) {
            return BotMessages.REGISTER_MESSAGE_WHEN_USER_LOGIN.getMessage();
        }

        Platform platform = msg.getPlatform();
        String userIdOnPlatform = msg.getUserIdOnPlatform();
        try {
            User user = new User(
                    0,
                    "User",
                    "Я есть user",
                    "login"+new Date().getTime()
            );
            userRepository.save(user);
            loggedUsersRepository.linkUserIdAndUserPlatform(
                    user.getId(),
                    platform,
                    userIdOnPlatform
            );
            userContextRepository.saveUserContext(
                    user.getId(),
                    new UserContext(UserState.EDIT_USER)
            );
            return BotMessages.REGISTER_MESSAGE.getMessage();
        } catch (SQLException | DBException e) {
            return "Что-то пошло не так"+ e.getMessage();
        }
    }
    /**
     */
    public String login(Message message) {
        try {
            return loginService.startSession(message.getPlatform(), message.getUserIdOnPlatform());
        } catch (DBException e) {
            return "проблемы с базой данных" + e.getMessage();
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
        } catch (SQLException | ParseException | DBException e) {
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
            StringBuilder allOrderUser;
            allOrderUser = new StringBuilder();
            for (Order s : listAllOrder) {
                if (
                        s.getCreatorId() == idUser
                        && s.getStatus() != OrderStatus.CLOSED
                        && s.getStatus() != OrderStatus.NOT_CLOSED
                )
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
      * и такие что курьер c userId не был равен {@link Order#getCreatorId()}  заказчику и выводит их
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
                return "Нет ни одного заказа, готового к выполнению";
            return allOrderUser.toString();

        } catch (SQLException | ParseException e) {
            return "что-то пошло не так" + e.getMessage();
        }
    }

    /**
     * @param userId Добавляет пользователя с этим userId в контекст {@link UserState#ORDER_ACCEPT ORDER_ACCEPTING}
     * @return Выводит сообщение с просьбой ввести курьера userId заказа, который он хочется принять 
     */
    public String startAcceptOrder(long userId){
        if (showPendingOrders(userId).equals("Нет ни одного заказа, готового к выполнению"))
            return "Нет ни одного заказа, готового к выполнению";
        UserContext userContext = new UserContext(UserState.ORDER_ACCEPT);
        try {
            userContextRepository.updateUserContext(userId, userContext);
        } catch (SQLException e) {
            return "Ошибка при обращении к базе данных." + e.getMessage();
        }

        return "Введите заказ который хотите принять";
    }

    /**
     * Выбирает из списка всех заказов заказы с состоянием {@link OrderStatus#RUNNING RUNNING} 
     * и такие что userId курьера = {@link Order#getCourierId()}  courierId и выводит их
     * @param userId курьер, который хочет посмотреть свои заказы
     */
    public String showAcceptOrder(long userId) {
        try {
            ArrayList<Order> listAllOrder = orderRepository.getAll();
            StringBuilder allOrderUser = new StringBuilder();
            UserContext client;
            for (Order s: listAllOrder){
                client = userContextRepository.getUserContext(s.getId());
                if(userId == s.getCourierId() && s.getStatus().equals(OrderStatus.RUNNING)){
                    allOrderUser.append(
                            Long.toString(s.getId()).concat(": ")
                                    .concat(s.getDescription()).concat("\n")
                    );
                }
            }
            if (allOrderUser.isEmpty())
                return "Нет ни одного заказа, готового к выполнению";
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
        if (showAcceptOrder(userId).equals("Нет ни одного заказа, готового к выполнению"))
            return "Нет ни одного заказа, готового к выполнению";
        UserContext userContext = new UserContext(UserState.ORDER_CLOSING_COURIER);
        try {
            userContextRepository.updateUserContext(userId, userContext);
        } catch (SQLException e) {
            return "Ошибка при обращении к базе данных." + e.getMessage();
        }


        return "Введите заказ который хотите завершить";
    }


}
