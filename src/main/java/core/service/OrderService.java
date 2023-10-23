package core.service;

import db.OrderRepository;
import db.UserContextRepository;
import models.Order;
import models.User;
import models.UserContext;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Сервис заказов
 */
public class OrderService {
    private final UserContextRepository userContextRepository;
    private final OrderRepository orderRepository;
    public OrderService(UserContextRepository userContextRepository, OrderRepository orderRepository) {
        this.userContextRepository = userContextRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Начало создание заказа. <br>
     * Создает заказ и добавляет в таблицу order так же добавляет контекст в таблицу userContext
     * @param idUser long
     * @return возвращает сообщение для дальнейшего создания заказа
     */
    public String startCreateOrder(long idUser){
        try {
            Order order = orderRepository.save(new Order(idUser));
            orderRepository.updateOrderStatus(order.getId(),"updating");
            UserContext userContext = new UserContext("create_order",0);
            userContextRepository.saveUserContext(idUser,userContext);
            return "Введите список продуктов";
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так";
        }
    }

    /**
     * Продолжение создания заказа. Диалог с пользователем<br><br>
     * 1)Получает контекст пользователя и заказ, создает новый заказ с описанием. Обновляет заказ в бд<br>
     * Удаляет контекст пользователя и меняет статус заказа так как создание закончилось
     * @param idUser long
     * @param text string список продуктов
     * @return сообщение об успешном создании заказа
     */
    public String continueCreateOrder(long idUser,String text){
        try {
            UserContext userContext = userContextRepository.getUserContext(idUser);
            if (userContext.getState_num() == 0){
                Order order = orderRepository.getOrderByIdUserAndStatus(idUser,"updating");
                order.setDescription(text);
                orderRepository.updateWithId(order);
                orderRepository.updateOrderStatus(order.getId(),"pending");
                userContextRepository.deleteUserContext(idUser);
                return "Заказ создан";
            }
            else
                return "Выход за пределы контекста";
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так";
        }
    }
    /**
     * Начало обновления заказа.  <br>
     * Получает список всех заказов и из них выбирает заказы пользователя
     * @param idUser order
     * @return Выводит список всех заказов пользователя, что бы пользователь мог выбрать какой заказ обновить
     */
    public String startUpdateOrder(long idUser) {
        try {
            ArrayList<Order> listAllOrder = orderRepository.getAll();
            if (listAllOrder.isEmpty()) return "у вас нет ни одного заказа";
            StringBuilder allOrderUser = new StringBuilder();
            for(Order s: listAllOrder) {
                if(s.getCreatorId() == idUser)
                    allOrderUser.append(Long.toString(s.getId()).concat("\n"));
            }
            UserContext userContext = new UserContext("update_order",0);
            userContextRepository.saveUserContext(idUser,userContext);
            return "Какой заказ вы хотите обновить.?\n"
                    .concat(allOrderUser.toString());
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так";
        }
    }

    /** Продолжение обновления. Диалог с пользователем<br>
     *
     * 1) получает от пользователя text = idOrder по нему смотрим есть ли такие заказы и если есть
     * обновляем статус заказа до update

     * 2) получает от пользователя text = список продуктов. Получаем заказ по idUser и status
     * Создаем новый заказ и меняем в нем описание. Обнвовляем заказ в бд.
     * Удаляем контекст пользователя и меняем статус заказа так как изменение заказа завершено
     * @param idUser long
     * @param text string 1)idOrder <br>
     *                    2)список продуктов
     * @return 1)выводим сообщение с просьбой ввести данные <br>
     * 2)выводим сообщение об окончании изменения заказа
     */
    public String continueUpdateOrder(long idUser, String text) {
        try {
            UserContext userContext = userContextRepository.getUserContext(idUser);
            switch (userContext.getState_num()){
                case 0 ->{
                    if (!text.chars().allMatch(Character::isDigit) && text.length()>18)
                        return "Заказ не найден. Попробуйте еще раз";
                    long idOrder = Long.parseLong(text);
                    Order order = orderRepository.getById(idOrder);
                    if (order==null)
                        return "Заказ не найден. Попробуйте еще раз";
                    orderRepository.updateOrderStatus(order.getId(),"updating");

                    Order orderCheck = orderRepository.getOrderByIdUserAndStatus(idUser,"updating");
                    if(orderCheck == null){
                        orderRepository.updateOrderStatus(order.getId(),"pending");
                        return "Заказ не найден. Поробуйте еще раз";
                    }
                    userContext.setStateNum(userContext.getState_num()+1);
                    userContextRepository.updateUserContext(idUser, userContext);
                    return "Напишите новый список продуктов";
                }
                case 1 ->{
                    Order order = orderRepository.getOrderByIdUserAndStatus(idUser,"updating");
                    order.setDescription(text);
                    orderRepository.updateWithId(order);
                    orderRepository.updateOrderStatus(order.getId(),"pending");
                    userContextRepository.deleteUserContext(idUser);
                    return "Заказ изменен";
                }
                default -> {return "Выход за пределы контекста";}
            }
        } catch (SQLException | ParseException e) {
            return "что-то пошлое не так";
        }
    }
    /**
     * Начало удаления заказа.  <br>
     * Получает список всех заказов и из них выбирает заказы пользователя
     * @param idUser long
     * @return Выводит список всех заказов пользователя, что бы пользователь мог выбрать какой заказ удалить
     */
    public String startСancelOrder(long idUser) {
        try {
            ArrayList<Order> listAllOrder = orderRepository.getAll();
            if (listAllOrder.isEmpty())
                return "у вас нет ни одного заказа";
            StringBuilder allOrderUser = new StringBuilder();
            for(Order s: listAllOrder) {
                if(s.getCreatorId() == idUser)
                    allOrderUser.append(Long.toString(s.getId()).concat("\n"));
            }
            UserContext userContext = new UserContext("cancel_order",0);
            userContextRepository.saveUserContext(idUser,userContext);
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
     * @param idUser long
     * @param text string
     * @return сообщение об успешном удалении заказа
     */
    public String continueСancelOrder(long idUser,String text) {
        try {
            UserContext userContext = userContextRepository.getUserContext(idUser);
            if (userContext.getState_num() == 0){
                long idOrder = Long.parseLong(text);
                if (!text.chars().allMatch(Character::isDigit)
                        && orderRepository.getById(idOrder)==null
                        && text.length()>18)
                    return "Заказ не найден. Попробуйте еще раз";
                Order order = orderRepository.getById(idOrder);
                orderRepository.updateOrderStatus(order.getId(),"updating");

                Order orderCheck = orderRepository.getOrderByIdUserAndStatus(idUser,"updating");
                if(orderCheck == null){
                    orderRepository.updateOrderStatus(order.getId(),"pending");
                    return "Заказ не найден. Поробуйте еще раз/";
                }

                orderRepository.delete(order.getId());
                userContextRepository.deleteUserContext(idUser);
                return "Заказ удален";
            }
            else
                return "Выход за пределы контекста ";
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так";
        }
    }
    /**
     * @param idUser long
     * @return Возвращает список все заказов пользователя с их описанием
     */
    public String viewListOrder(long idUser) {
        try {
            ArrayList<Order> listAllOrder = orderRepository.getAll();
            if (listAllOrder.isEmpty()) return "у вас нет ни одного заказов";
            StringBuilder allOrderUser = new StringBuilder();
            for(Order s: listAllOrder) {
                if(s.getCreatorId() == idUser)
                    allOrderUser.append(
                        Long.toString(s.getId()).concat(": ")
                        .concat(s.getDescription()).concat("\n")
                    );
            }
            return allOrderUser.toString();
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так";
        }
    }
}