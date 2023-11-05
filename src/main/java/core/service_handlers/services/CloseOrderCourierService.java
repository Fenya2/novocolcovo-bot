package core.service_handlers.services;

import core.MessageSender;
import db.OrderRepository;
import db.UserContextRepository;
import models.Order;
import models.OrderStatus;
import models.UserContext;
import models.UserState;

import java.sql.SQLException;
import java.text.ParseException;

/** Сервис для работы с контекстом {@link models.UserState#ORDER_CLOSING_COURIER ORDER_CLOSING_COURIER}**/
public class CloseOrderCourierService {

    private MessageSender messageSender;

    /** @see OrderRepository */
    private final OrderRepository orderRepository;

    /** @see UserContextRepository */
    private final UserContextRepository userContextRepository;

    /** Конструктор {@link CloseOrderCourierService}*/
    public CloseOrderCourierService(OrderRepository orderRepository, UserContextRepository userContextRepository) {
        this.orderRepository = orderRepository;
        this.userContextRepository = userContextRepository;
    }


    /**
     * Меняет статус заказа на {@link models.OrderStatus#CLOSING CLOSING}<br>
     * Меняет контекст курьера(userId) на {@link models.UserState#NO_STATE NO_STATE} <br>
     * Если заказчик находится в контексте  {@link models.UserState#NO_STATE NO_STATE}, то
     * Меняет контекст заказчика(создателя заказа) на {@link models.UserState#ORDER_CLOSING_CLIENT}
     * Иначе не дает завершить заказ <br>
     * Отправляет сообщение, что заказ принят или ошибку
     * TODO заказчику приходит сообщение о завершении заказа и инструкция дальнейших действий
     * @param userId id курьера
     * @param text id заказа
     */
    public String continueSession(long userId, String text) {
        try {
            UserContext userContextCourier = userContextRepository.getUserContext(userId);
            if (userContextCourier.getStateNum() == 0) {
                if(!validation(userId,text))
                    return "Заказ не найден. Попробуйте еще раз";

                long idOrder = Long.parseLong(text);
                Order order = orderRepository.getById(idOrder);
                UserContext userContextClient = userContextRepository.getUserContext(order.getCreatorId());
                if(userContextClient.getState() == UserState.NO_STATE) {
                    userContextRepository.updateUserContext(
                            order.getCreatorId(),
                            new UserContext(UserState.ORDER_CLOSING_CLIENT)
                    );
                }
                else{
                    return "Заказчик не может сейчас завершить заказ, попробуйте позже";
                }
                orderRepository.updateOrderStatus(idOrder, OrderStatus.CLOSING);
                userContextRepository.updateUserContext(userId,new UserContext());

                messageSender.sendTextMessage(
                        order.getCreatorId(),
                        "Подтвердите что ваш заказ приняли, написав /yes /no."
                );
                return "Завершение заказа отправлено на подтверждение заказчику";
            } else
                return "Выход за пределы контекста";
        } catch (SQLException | ParseException e) {
            return "что-то пошло не так";
        }
    }

    /**
     * Проверяет можно принять введенный заказ или нет
     * @param userId id курьера
     * @param text id заказа
     * @return true/false может/не может принять введенный заказ
     */
    private boolean validation(long userId, String text) throws SQLException, ParseException {
        if (!text.chars().allMatch(Character::isDigit) || text.length() > 18)
            return false;
        long idOrder = Long.parseLong(text);
        Order order = orderRepository.getById(idOrder);
        if (order == null)
            return false;

        if (userId==order.getCreatorId()) {
            return false;
        }
        return true;
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }
}
