package new_core.service_handlers.services;

import db.OrderRepository;
import db.UserContextRepository;
import models.Order;
import models.OrderStatus;
import models.UserContext;

import java.sql.SQLException;
import java.text.ParseException;

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

    /**
     * Меняет у введенного курьером заказа статус на {@link models.OrderStatus#RUNNING RUNNING} <br>
     * Меняет поле {@link models.Order#courierId courierId} на id курьера <br>
     * Меняет контекст курьера на {@link models.UserState#NO_STATE NO_STATE}
     * Отправляет сообщение, что заказ принят или ошибку
     * TODO так же должен отправить сообщение заказчику что его заказ приняли
     * @param userId id курьера
     * @param text id заказа
     */
    public String continueSession(long userId, String text) {
        try {
            UserContext userContext = userContextRepository.getUserContext(userId);
            if (userContext.getStateNum() == 0) {
                if(!validation(userId,text))
                    return "Заказ не найден. Попробуйте еще раз";

                long idOrder = Long.parseLong(text);
                orderRepository.updateOrderStatus(idOrder,OrderStatus.RUNNING);
                Order order = orderRepository.getById(idOrder);
                order.setCourierId(idOrder);
                orderRepository.update(order);
                userContextRepository.updateUserContext(userId,new UserContext());
                return "Заказ удален";
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

}
