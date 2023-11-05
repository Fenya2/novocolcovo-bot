package new_core.service_handlers.services;

import db.OrderRepository;
import db.UserContextRepository;
import models.Order;
import models.OrderStatus;
import models.UserContext;

import java.sql.SQLException;
import java.text.ParseException;

/** Сервис для работы с контекстом {@link models.UserState#ORDER_CLOSING_CLIENT ORDER_CLOSING_CLIENT}**/
public class CloseOrderClientService {
    /** @see OrderRepository */
    private final OrderRepository orderRepository;

    /** @see UserContextRepository */
    private final UserContextRepository userContextRepository;

    /** Конструктор {@link CloseOrderCourierService}*/

    public CloseOrderClientService(OrderRepository orderRepository, UserContextRepository userContextRepository) {
        this.orderRepository = orderRepository;
        this.userContextRepository = userContextRepository;
    }

    /**
     * Пользователю(заказчику) приходит сообщение с просьбой подтвердить заказ. <br>
     * Eсли он отвечает /yes то статус заказа переходит в состояние {@link models.OrderStatus#CLOSED CLOSED} <br>
     * Eсли заказчик отвечает /no то статус заказа переходит в состояние {@link models.OrderStatus#NOT_CLOSED NOT_CLOSED} <br>
     * В обоих случаях заказчик переходит в контекст {@link models.UserState#NO_STATE NO_STATE}
     * если это не /yes и не /no, то выводит сообщение о не корректном сообщении
     * @param userId заказчик
     * @param text ответ заказчика
     * @return вернет сообщение об успешном закрытие заказа,
     * иначе скажет что заказ не закрыт и посоветует решить вопрос с курьером лично
     */
    public String continueSession(long userId, String text){
        try {
            if (text.equals("/yes")) {
                Order order = orderRepository.getOrderByIdUserAndStatus(userId, OrderStatus.CLOSING);
                orderRepository.updateOrderStatus(order.getId(), OrderStatus.CLOSED);
                userContextRepository.updateUserContext(userId,new UserContext());
                return "Заказ успешно закрыт";
            } else if (text.equals("/no")) {
                Order order = orderRepository.getOrderByIdUserAndStatus(userId, OrderStatus.CLOSING);
                orderRepository.updateOrderStatus(order.getId(), OrderStatus.NOT_CLOSED);
                return "Заказ не закрыт. Свяжитесь с курьером";
            } else {
                return "Извините я вас не понимаю. Напиши /help";
            }
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так";
        }
    }
}
