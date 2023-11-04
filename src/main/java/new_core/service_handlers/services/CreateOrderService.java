package new_core.service_handlers.services;

import db.OrderRepository;
import db.UserContextRepository;
import models.Order;
import models.OrderStatus;
import models.UserContext;

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
                orderRepository.updateWithId(order);
                orderRepository.updateOrderStatus(order.getId(), OrderStatus.PENDING);
                userContextRepository.updateUserContext(userId,new UserContext());
                return "Заказ создан";
            } else
                return "Выход за пределы контекста";
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так";
        }
    }
}
