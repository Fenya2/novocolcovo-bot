package new_core.service_handlers.services;

import db.OrderRepository;
import db.UserContextRepository;
import models.Order;
import models.OrderStatus;
import models.UserContext;
import models.UserState;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

public class CancelOrderService {
    private final OrderRepository orderRepository;
    private final UserContextRepository userContextRepository;
    public CancelOrderService(OrderRepository orderRepository, UserContextRepository userContextRepository) {
        this.orderRepository = orderRepository;
        this.userContextRepository = userContextRepository;
    }

    public String continueSession(long userId, String text){
        try {
            UserContext userContext = userContextRepository.getUserContext(userId);
            if (userContext.getStateNum() == 0) {
                if(text.length() > 18 || !text.chars().allMatch(Character::isDigit))
                    return "Заказ не найден. Попробуйте еще раз(1)";
                long idOrder = Long.parseLong(text);
                Order order = orderRepository.getById(idOrder);
                if (order == null)
                    return "Заказ не найден. Попробуйте еще раз(2)";

                orderRepository.updateOrderStatus(order.getId(), OrderStatus.UPDATING);
                Order orderCheck = orderRepository.getOrderByIdUserAndStatus(userId, OrderStatus.UPDATING);
                if (orderCheck == null) {
                    orderRepository.updateOrderStatus(order.getId(), OrderStatus.PENDING);
                    return "Заказ не найден. Попробуйте еще раз(3)";
                }

                orderRepository.delete(order.getId());
                userContextRepository.updateUserContext(userId,new UserContext());
                return "Заказ удален";
            } else
                return "Выход за пределы контекста";
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так";
        }
    }
}