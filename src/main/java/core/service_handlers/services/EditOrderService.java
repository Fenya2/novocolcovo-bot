package core.service_handlers.services;

import db.DBException;
import db.OrderRepository;
import db.UserContextRepository;
import models.Order;
import models.OrderStatus;
import models.UserContext;

import java.sql.SQLException;
import java.text.ParseException;

/** Сервис для работы с контекстом {@link models.UserState#ORDER_EDITING ORDER_EDITING}*/

public class EditOrderService {

    /** @see OrderRepository*/
    private final OrderRepository orderRepository;

    /** @see UserContextRepository*/
    private final UserContextRepository userContextRepository;

    /** Конструктор {@link EditOrderService}*/
    public EditOrderService(OrderRepository orderRepository, UserContextRepository userContextRepository) {
        this.orderRepository = orderRepository;
        this.userContextRepository = userContextRepository;
    }

    /**
     * Проверяет можно принять введенный заказ или нет
     * @param userId id курьера
     * @param text id заказа
     * @return true/false может/не может принять введенный заказ
     */
    private boolean validation(long userId, String text) throws SQLException, ParseException, DBException {
        if (!text.chars().allMatch(Character::isDigit) || text.length() > 18)
            return false;
        long idOrder = Long.parseLong(text);
        Order order = orderRepository.getById(idOrder);
        if (order == null)
            return false;
        if(!(order.getStatus()==(OrderStatus.PENDING)))
            return false;
        orderRepository.updateOrderStatus(order.getId(), OrderStatus.UPDATING);
        Order orderCheck = orderRepository.getOrderByIdUserAndStatus(userId, OrderStatus.UPDATING);
        if (orderCheck == null) {
            orderRepository.updateOrderStatus(order.getId(), OrderStatus.PENDING);
            return false;
        }
        return true;
    }
    /**
     * Продолжение обновления. Диалог с пользователем<br>
     * <p>
     * 1) получает от пользователя text = idOrder по нему смотрим есть ли такие заказы и если есть
     * обновляем статус заказа до edit
     * <p>
     * 2) получает от пользователя text = список продуктов. Получаем заказ по idUser и status.
     * Создает новый заказ и меняет в нем описание. Обновляет заказ в бд.
     * Удаляет контекст пользователя и меняет статус заказа так как изменение заказа завершено
     *
     * @param userId id пользователя, который изменяет заказ
     * @param text   string 1)idOrder <br>
     *               2)список продуктов
     * @return 1)выводит сообщение с просьбой ввести данные <br>
     * 2)выводит сообщение об окончании изменения заказа
     */
    public String continueSession(long userId, String text) {
        try {
            UserContext userContext = userContextRepository.getUserContext(userId);
            switch (userContext.getStateNum()) {
                case 0 -> {
                    if(!validation(userId,text))
                        return "Заказ не найден или выполняется курьером. Попробуй еще раз";
                    userContext.incrementStateNum();
                    userContext.setStateNum(userContext.getStateNum());
                    userContextRepository.updateUserContext(userId, userContext);
                    return "Напиши новый список продуктов";
                }
                case 1 -> {
                    Order order = orderRepository.getOrderByIdUserAndStatus(userId, OrderStatus.UPDATING);
                    order.setDescription(text);
                    userContext.incrementStateNum();
                    orderRepository.update(order);
                    orderRepository.updateOrderStatus(order.getId(), OrderStatus.PENDING);
                    userContextRepository.updateUserContext(userId, new UserContext());
                    return "Заказ изменен";
                }
                default -> {
                    return "Выход за пределы контекста";
                }
            }
        } catch (SQLException | ParseException | DBException e) {
            return "что-то пошлое не так" + e.getMessage();
        }
    }

    /**
     * Возвращает пользователя в контекст {@link models.UserState#NO_STATE NO_STATE}.<br>
     * Выводит "ok" если команда выполнена успешно, иначе сообщение об ошибке
     */
    public String cancel(long userId){
        try {
            userContextRepository.updateUserContext(userId,new UserContext());
            return "Поздравляю, ты вернулся назад!";
        } catch (SQLException e) {
            return e.getMessage();
        }
    }
}