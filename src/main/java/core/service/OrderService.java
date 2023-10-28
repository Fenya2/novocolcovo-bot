package core.service;

import db.OrderRepository;
import db.UserContextRepository;
import models.Order;
import models.OrderStatus;
import models.UserContext;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

/** Сервис заказов */
public class OrderService {
    /** Для работы с таблицей контекстов пользователя. */
    private final UserContextRepository userContextRepository;
    /** Для работы с таблицей заказов. */
    private final OrderRepository orderRepository;

    /**
     * @param userContextRepository Для работы с таблицей контекстов пользователя.
     * @param orderRepository Для работы с таблицей заказов.
     */
    public OrderService(UserContextRepository userContextRepository, OrderRepository orderRepository) {
        this.userContextRepository = userContextRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Начало создания заказа. <br>
     * Создает заказ и добавляет в таблицу order, обновляет статус у заказа
     * и добавляет контекст в таблицу userContext.
     *
     * @param idUser идентификатор пользователя.
     * @return Сообщение для дальнейшего создания заказа.
     * todo может число, либо true False. А обработчик сгенерирует сообщение по ответу сервиса?
     */
    public String startCreateOrder(long idUser) {
        try {
            Order order = orderRepository.save(new Order(idUser));
            orderRepository.updateOrderStatus(order.getId(), OrderStatus.UPDATING);
            UserContext userContext = new UserContext("create_order", 0);
            userContextRepository.saveUserContext(idUser, userContext);
            return "Введите список продуктов";
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так";
        }
    }

    /**
     * Продолжение создания заказа. Диалог с пользователем<br><br>
     * Получает контекст пользователя и заказ, создает новый заказ с описанием. Обновляет заказ в бд<br>
     * Удаляет контекст пользователя и меняет статус заказа так как создание закончилось
     *
     * @param idUser long
     * @param text   string список продуктов
     * @return сообщение об успешном создании заказа
     */
    public String continueCreateOrder(long idUser, String text) {
        try {
            UserContext userContext = userContextRepository.getUserContext(idUser);
            if (userContext.getState_num() == 0) {
                Order order = orderRepository.getOrderByIdUserAndStatus(idUser, OrderStatus.UPDATING);
                order.setDescription(text);
                orderRepository.updateWithId(order);
                orderRepository.updateOrderStatus(order.getId(), OrderStatus.PENDING);
                userContextRepository.deleteUserContext(idUser);
                return "Заказ создан";
            } else
                return "Выход за пределы контекста";
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
            UserContext userContext = new UserContext("edit_order", 0);
            userContextRepository.saveUserContext(idUser, userContext);
            return "Какой заказ вы хотите обновить.?\n"
                    .concat(allOrderUser.toString());
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так";
        }
    }

    /**
     * Продолжение обновления. Диалог с пользователем<br>
     * <p>
     * 1) получает от пользователя text = idOrder по нему смотрим есть ли такие заказы и если есть
     * обновляем статус заказа до edit
     * <p>
     * 2) получает от пользователя text = список продуктов. Получаем заказ по idUser и status
     * Создаем новый заказ и меняем в нем описание. Обнвовляем заказ в бд.
     * Удаляем контекст пользователя и меняем статус заказа так как изменение заказа завершено
     *
     * @param idUser long
     * @param text   string 1)idOrder <br>
     *               2)список продуктов
     * @return 1)выводим сообщение с просьбой ввести данные <br>
     * 2)выводим сообщение об окончании изменения заказа
     */
    public String continueEditOrder(long idUser, String text) {
        try {
            UserContext userContext = userContextRepository.getUserContext(idUser);
            switch (userContext.getState_num()) {
                case 0 -> {
                    if (!text.chars().allMatch(Character::isDigit) || text.length() > 18)
                        return "Заказ не найден. Попробуйте еще раз(1)";
                    long idOrder = Long.parseLong(text);
                    Order order = orderRepository.getById(idOrder);
                    if (order == null)
                        return "Заказ не найден. Попробуйте еще раз(2)";

                    orderRepository.updateOrderStatus(order.getId(), OrderStatus.UPDATING);
                    Order orderCheck = orderRepository.getOrderByIdUserAndStatus(idUser, OrderStatus.UPDATING);
                    if (orderCheck == null) {
                        orderRepository.updateOrderStatus(order.getId(), OrderStatus.PENDING);
                        return "Заказ не найден. Попробуйте еще раз(3)";
                    }

                    userContext.setStateNum(userContext.getState_num() + 1);
                    userContextRepository.updateUserContext(idUser, userContext);
                    return "Напишите новый список продуктов";
                }
                case 1 -> {
                    Order order = orderRepository.getOrderByIdUserAndStatus(idUser, OrderStatus.UPDATING);
                    order.setDescription(text);
                    orderRepository.updateWithId(order);
                    orderRepository.updateOrderStatus(order.getId(), OrderStatus.PENDING);
                    userContextRepository.deleteUserContext(idUser);
                    return "Заказ изменен";
                }
                default -> {
                    return "Выход за пределы контекста";
                }
            }
        } catch (SQLException | ParseException e) {
            return "что-то пошлое не так";
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

            UserContext userContext = new UserContext("cancel_order", 0);
            userContextRepository.saveUserContext(idUser, userContext);
            return "Какой заказ вы хотите удалить.?\n"
                    .concat(allOrderUser.toString());
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так";
        }
    }

    /**
     * Продолжение удаления заказа. Диалог с пользователем<br><br>
     * 1)Получает контекст пользователя и заказ, удаляет заказ в бд<br>
     * Удаляет контекст пользователя и меняет статус заказа так как создание закончилось
     *
     * @param idUser long
     * @param text   string
     * @return сообщение об успешном удалении заказа
     */
    public String continueCancelOrder(long idUser, String text) {
        try {
            UserContext userContext = userContextRepository.getUserContext(idUser);
            if (userContext.getState_num() == 0) {
                if(text.length() > 18 || !text.chars().allMatch(Character::isDigit))
                    return "Заказ не найден. Попробуйте еще раз(1)";
                long idOrder = Long.parseLong(text);
                Order order = orderRepository.getById(idOrder);
                if (order == null)
                    return "Заказ не найден. Попробуйте еще раз(2)";

                orderRepository.updateOrderStatus(order.getId(), OrderStatus.UPDATING);
                Order orderCheck = orderRepository.getOrderByIdUserAndStatus(idUser, OrderStatus.UPDATING);
                if (orderCheck == null) {
                    orderRepository.updateOrderStatus(order.getId(), OrderStatus.PENDING);
                    return "Заказ не найден. Попробуйте еще раз(3)";
                }

                orderRepository.delete(order.getId());
                userContextRepository.deleteUserContext(idUser);
                return "Заказ удален";
            } else
                return "Выход за пределы контекста";
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