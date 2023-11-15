package core.service_handlers.services;

import db.DBException;
import db.OrderRepository;
import db.UserContextRepository;
import models.Order;
import models.OrderStatus;
import models.UserContext;

import javax.xml.crypto.dsig.dom.DOMValidateContext;
import java.sql.SQLException;
import java.text.ParseException;

/** Сервис для работы с контекстом {@link models.UserState#ORDER_CREATING ORDER_CREATING}*/
public class CreateOrderService {

    /** @see OrderRepository*/
    private final OrderRepository orderRepository;

    /** @see UserContextRepository*/
    private final UserContextRepository userContextRepository;

    /** Конструктор {@link CreateOrderService}*/
    public CreateOrderService(OrderRepository orderRepository, UserContextRepository userContextRepository) {
        this.orderRepository = orderRepository;
        this.userContextRepository = userContextRepository;
    }

    /**
     * Продолжение создания заказа. Диалог с пользователем<br><br>
     * Получает контекст пользователя и заказ, создает новый заказ с описанием. Обновляет заказ в бд<br>
     * Удаляет контекст пользователя и меняет статус заказа так как создание закончилось
     *
     * @param userId id пользователя, который создает заказ
     * @param text   string список продуктов
     * @return сообщение об успешном создании заказа
     */
    public String continueSession(long userId, String text) {
        try {
            UserContext userContext = userContextRepository.getUserContext(userId);
            if (userContext.getStateNum() == 0) {
                Order order = orderRepository.getOrderByIdUserAndStatus(userId, OrderStatus.UPDATING);
                order.setDescription(text);
                orderRepository.update(order);
                orderRepository.updateOrderStatus(order.getId(), OrderStatus.PENDING);
                userContextRepository.updateUserContext(userId,new UserContext());
                return "Заказ создан";
            } else
                return "Выход за пределы контекста";
        } catch (SQLException | ParseException | DBException e) {
            return "что-то пошло не так";
        }
    }

    /**
     * Удаляет созданный заказ.
     * Возвращает пользователя в контекст {@link models.UserState#NO_STATE NO_STATE}.<br>
     * Выводит "ok" если команда выполнена успешно, иначе сообщение об ошибке
     */
    public String cancel(long userId){
        try {
            Order order = orderRepository.getOrderByIdUserAndStatus(userId,OrderStatus.UPDATING);
            orderRepository.delete(order.getId());
            userContextRepository.updateUserContext(userId,new UserContext());
            return "Поздравляю, ты вернулся назад!";
        } catch (SQLException | ParseException e) {
            return e.getMessage();
        }
    }
}
