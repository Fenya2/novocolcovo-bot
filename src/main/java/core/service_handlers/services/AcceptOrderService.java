package core.service_handlers.services;

import db.OrderRepository;
import db.UserContextRepository;

/** Сервис для работы с контекстом {@link models.UserState#ORDER_ACCEPTING ORDER_ACCEPTING}*/
public class AcceptOrderService {

    /** @see OrderRepository */
    private final OrderRepository orderRepository;

    /** @see UserContextRepository */
    private final UserContextRepository userContextRepository;

    /** Конструктор {@link AcceptOrderService}*/
    public AcceptOrderService(OrderRepository orderRepository, UserContextRepository userContextRepository) {
        this.orderRepository = orderRepository;
        this.userContextRepository = userContextRepository;
    }

    public String continueSession(long id, String text) {
        return null;
    }

}
