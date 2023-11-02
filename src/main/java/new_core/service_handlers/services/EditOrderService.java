package new_core.service_handlers.services;

import db.OrderRepository;
import db.UserContextRepository;
import models.Order;
import models.OrderStatus;
import models.UserContext;

import java.sql.SQLException;
import java.text.ParseException;

public class EditOrderService {
    private final OrderRepository orderRepository;
    private final UserContextRepository userContextRepository;
    public EditOrderService(OrderRepository orderRepository, UserContextRepository userContextRepository) {
        this.orderRepository = orderRepository;
        this.userContextRepository = userContextRepository;
    }

    public String continueSession(long userId, String text) {
        try {
            UserContext userContext = userContextRepository.getUserContext(userId);
            switch (userContext.getStateNum()) {
                case 0 -> {
                    if (!text.chars().allMatch(Character::isDigit) || text.length() > 18)
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

                    userContext.setStateNum(userContext.getStateNum() + 1);
                    userContextRepository.updateUserContext(userId, userContext);
                    return "Напишите новый список продуктов";
                }
                case 1 -> {
                    Order order = orderRepository.getOrderByIdUserAndStatus(userId, OrderStatus.UPDATING);
                    order.setDescription(text);
                    orderRepository.updateWithId(order);
                    orderRepository.updateOrderStatus(order.getId(), OrderStatus.PENDING);
                    userContextRepository.updateUserContext(userId,new UserContext());
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

}