package core.service_handlers.services;

import core.MessageSender;
import db.OrderRepository;
import db.UserContextRepository;
import models.Order;
import models.OrderStatus;
import models.UserContext;

import java.sql.SQLException;
import java.text.ParseException;

/** Сервис для работы с контекстом {@link models.UserState#ORDER_ACCEPTING_CLIENT ORDER_ACCEPTING_CLIENT}**/
public class AcceptOrderClientService {

    /** @see MessageSender */
    private MessageSender messageSender;

    /** @see OrderRepository */
    private final OrderRepository orderRepository;

    /** @see UserContextRepository */
    private final UserContextRepository userContextRepository;

    /** Конструктор {@link AcceptOrderClientService}*/
    public AcceptOrderClientService(OrderRepository orderRepository, UserContextRepository userContextRepository) {
        this.orderRepository = orderRepository;
        this.userContextRepository = userContextRepository;
    }

    /**
     * Пользователю(заказчику) приходит сообщение с просьбой подтвердить заказ. <br>
     * Eсли он отвечает /yes то статус заказа переходит в состояние {@link models.OrderStatus#RUNNING RUNNING} <br>
     * Eсли заказчик отвечает /no то статус заказа переходит в состояние {@link models.OrderStatus#PENDING PENDING} <br>
     * В обоих случаях заказчик переходит в контекст {@link models.UserState#NO_STATE NO_STATE}
     * если это не /yes и не /no, то выводит сообщение о не корректном сообщении
     * @param userId заказчик
     * @param text ответ заказчика
     * @return вернет сообщение об успешном закрытие заказа,
     * иначе скажет что заказ не закрыт и посоветует решить вопрос с курьером лично
     */
    public String continueSession(long userId, String text){
        try {
            Order order = orderRepository.getOrderByIdUserAndStatus(userId, OrderStatus.ACCEPTING);
            if (text.equals("/yes")) {
                orderRepository.updateOrderStatus(order.getId(), OrderStatus.RUNNING);
                userContextRepository.updateUserContext(userId,new UserContext());
                messageSender.sendTextMessage(order.getCourierId(),"Заказ успешно принят");
                return "Заказ успешно принят";
            } else if (text.equals("/no")) {
                messageSender.sendTextMessage(order.getCourierId(),"Твой заказ не приняли:(");
                order.setStatus(OrderStatus.PENDING);
                order.setCourierId(0);
                orderRepository.update(order);
                userContextRepository.updateUserContext(userId,new UserContext());
                return "Заказ не принят.";
            } else {
                return "Прости, но я не знаю, что на это ответить. Вызови команду /help";
            }
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так";
        }
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

}
