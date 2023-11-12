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

/** Сервис для работы с контекстом {@link models.UserState#ORDER_ACCEPTING_COURIER ORDER_ACCEPTING}*/
public class AcceptOrderCourierService {

    /** @see MessageSender*/
    private MessageSender messageSender;

    /** @see OrderRepository */
    private final OrderRepository orderRepository;

    /** @see UserContextRepository */
    private final UserContextRepository userContextRepository;

    /** Конструктор {@link AcceptOrderCourierService}*/
    public AcceptOrderCourierService(OrderRepository orderRepository, UserContextRepository userContextRepository) {
        this.orderRepository = orderRepository;
        this.userContextRepository = userContextRepository;
    }

    /**
     * Меняет у введенного курьером заказа статус на {@link models.OrderStatus#ACCEPTING ACCEPTING} <br>
     * Меняет поле {@link Order#getCourierId()}  courierId} на id курьера <br>
     * Меняет контекст курьера на {@link models.UserState#NO_STATE NO_STATE}
     * Отправляет сообщение, что заказ принят или ошибку
     * так же отправляет сообщение заказчику, что его заказ хотят принять
     * @param userId id курьера
     * @param text id заказа
     */
    public String continueSession(long userId, String text) {
        try {
            UserContext userContext = userContextRepository.getUserContext(userId);
            if (userContext.getStateNum() == 0) {
                if(!validation(userId,text))
                    return "Заказ не найден. Попробуй еще раз";

                long idOrder = Long.parseLong(text);
                Order order = orderRepository.getById(idOrder);
                UserContext userContextClient = userContextRepository.getUserContext(order.getCreatorId());
                if(userContextClient.getState() != UserState.NO_STATE){
                    messageSender.sendTextMessage(
                            order.getCreatorId(),
                            "Курьер хочет принять заказ, заверши выполнение команды."
                    );
                    return "Извини, но сейчас заказ нельзя принять.";
                }
                userContextRepository.updateUserContext(
                        order.getCreatorId(),
                        new UserContext(UserState.ORDER_ACCEPTING_CLIENT)
                );
                order.setCourierId(userId);
                order.setStatus(OrderStatus.ACCEPTING);
                orderRepository.update(order);
                userContextRepository.updateUserContext(userId, new UserContext());

                messageSender.sendTextMessage(
                        order.getCreatorId(),
                        "Подтвердите, что вы готовы отдать курьеру заказ, напив \n/yes /no"
                );
                return "Принятие заказа отправлено на подтверждение заказчику";
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

    /**
     * Возвращает пользователя в контекст {@link models.UserState#NO_STATE NO_STATE}.<br>
     * Выводит "ok" если команда выполнена успешно, иначе сообщение об ошибке
     */
    public String cancel(long userId){
        try {
            userContextRepository.updateUserContext(userId,new UserContext());
            return "Поздравляю, ты вернулся назад!";
        } catch (SQLException e) {
            return e.getMessage();
        }
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }
}
