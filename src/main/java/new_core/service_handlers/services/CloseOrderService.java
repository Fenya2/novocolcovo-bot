package new_core.service_handlers.services;

import db.OrderRepository;
import db.UserContextRepository;

/** Сервис для работы с контекстом {@link models.UserState#ORDER_CLOSING_COURIER ORDER_CLOSING_COURIER}
    и {@link models.UserState#ORDER_CLOSING_CLIENT ORDER_CLOSING_CLIENT}
 **/
public class CloseOrderService {

    /** @see OrderRepository */
    private final OrderRepository orderRepository;

    /** @see UserContextRepository */
    private final UserContextRepository userContextRepository;

    /** Конструктор {@link CloseOrderService}*/
    public CloseOrderService(OrderRepository orderRepository, UserContextRepository userContextRepository) {
        this.orderRepository = orderRepository;
        this.userContextRepository = userContextRepository;
    }

    public String continueSession(long id, String text) {
        return text;
    }
}
