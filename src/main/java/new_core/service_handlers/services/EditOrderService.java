package new_core.service_handlers.services;

import db.OrderRepository;
import db.UserContextRepository;
import models.Order;
import models.UserContext;
import models.UserState;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

public class EditOrderService extends Service {
    private final OrderRepository orderRepository;
    public EditOrderService(UserContextRepository userContextRepository, OrderRepository orderRepository) {
        super(userContextRepository);
        this.orderRepository = orderRepository;
    }

    @Override
    public String startSession(long userId) {
        try {
            ArrayList<Order> listAllOrder = orderRepository.getAll();
            StringBuilder allOrderUser = new StringBuilder();
            for (Order s : listAllOrder) {
                if (s.getCreatorId() == userId)
                    allOrderUser.append(Long.toString(s.getId()).concat(": ")
                            .concat(s.getDescription()).concat("\n"));
            }
            if (allOrderUser.isEmpty())
                return "у вас нет ни одного заказа";
            //TODO
            UserContext userContext = new UserContext(UserState.ORDER_EDITING, 0);
            userContextRepository.saveUserContext(userId, userContext);
            return "Какой заказ вы хотите обновить.?\n"
                    .concat(allOrderUser.toString());
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
