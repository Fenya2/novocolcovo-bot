package new_core.service_handlers.services;

import db.OrderRepository;
import db.UserContextRepository;
import models.Order;
import models.OrderStatus;
import models.UserContext;

import java.sql.SQLException;
import java.text.ParseException;

/** Сервис для работы с контекстом {@link models.UserState#ORDER_CANCELING ORDER_CANCELING}*/
public class CancelOrderService {

    /** @see OrderRepository*/
    private final OrderRepository orderRepository;

    /** @see UserContextRepository*/
    private final UserContextRepository userContextRepository;

    /** Конструктор {@link CancelOrderService}*/
    public CancelOrderService(OrderRepository orderRepository, UserContextRepository userContextRepository) {
        this.orderRepository = orderRepository;
        this.userContextRepository = userContextRepository;
    }

    /**
     * Продолжение удаления заказа. Диалог с пользователем<br><br>
     * 1)Получает контекст пользователя и заказ, удаляет заказ в бд<br>
     * Удаляет контекст пользователя и меняет статус заказа так как создание закончилось
     *
     * @param userId id пользователя, который удаляет заказ
     * @param text   string
     * @return сообщение об успешном удалении заказа
     */
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