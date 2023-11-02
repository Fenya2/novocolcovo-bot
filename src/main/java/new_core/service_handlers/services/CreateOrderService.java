package new_core.service_handlers.services;

import db.OrderRepository;
import db.UserContextRepository;
import models.Order;
import models.OrderStatus;
import models.UserContext;
import models.UserState;

import java.sql.SQLException;
import java.text.ParseException;

public class CreateOrderService {
    private final OrderRepository orderRepository;
    private final UserContextRepository userContextRepository;
    public CreateOrderService(OrderRepository orderRepository, UserContextRepository userContextRepository) {
        this.orderRepository = orderRepository;
        this.userContextRepository = userContextRepository;
    }

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
