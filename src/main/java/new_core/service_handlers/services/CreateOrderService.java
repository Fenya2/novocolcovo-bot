package new_core.service_handlers.services;

import db.OrderRepository;
import db.UserContextRepository;
import models.Order;
import models.OrderStatus;
import models.UserContext;
import models.UserState;

import java.sql.SQLException;
import java.text.ParseException;

public class CreateOrderService extends Service{
    private final OrderRepository orderRepository;
    public CreateOrderService(UserContextRepository userContextRepository, OrderRepository orderRepository) {
        super(userContextRepository);
        this.orderRepository = orderRepository;
    }

    @Override
    public String startSession(long userId) {
        try {
            Order order = orderRepository.save(new Order(userId));
            orderRepository.updateOrderStatus(order.getId(), OrderStatus.UPDATING);
            UserContext userContext = new UserContext(UserState.ORDER_CREATING, 0);
            userContextRepository.saveUserContext(userId, userContext);
            return "Введите список продуктов";
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так";
        }
    }

    @Override
    public String endSession(long userId) throws SQLException {
        return null;
    }

    @Override
    public String getHelpMessage() {
        return null;
    }
}
