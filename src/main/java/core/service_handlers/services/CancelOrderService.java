package core.service_handlers.services;

import core.UserNotifier;
import db.DBException;
import db.OrderRepository;
import db.UserContextRepository;
import models.Order;
import models.OrderStatus;
import models.UserContext;

import java.sql.SQLException;
import java.text.ParseException;

/** Сервис для работы с контекстом {@link models.UserState#ORDER_CANCELING ORDER_CANCELING}*/
public class CancelOrderService {

    /** @see UserNotifier */
    private UserNotifier userNotifier;

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
     * Проверяет можно принять введенный заказ или нет
     * @param userId id курьера
     * @param text id заказа
     * @return true/false может/не может принять введенный заказ
     */
    private boolean validation(long userId, String text) throws SQLException, ParseException, DBException {
        if (!text.chars().allMatch(Character::isDigit) || text.length() > 18)
            return false;
        long idOrder = Long.parseLong(text);
        Order order = orderRepository.getById(idOrder);
        if (order == null)
            return false;
        if(!(order.getStatus().equals(OrderStatus.PENDING)||
                order.getStatus().equals(OrderStatus.RUNNING))){
            return false;
        }
        orderRepository.updateOrderStatus(order.getId(), OrderStatus.UPDATING);
        Order orderCheck = orderRepository.getOrderByIdUserAndStatus(userId, OrderStatus.UPDATING);
        if (orderCheck == null) {
            orderRepository.updateOrderStatus(order.getId(), OrderStatus.PENDING);
            return false;
        }
        return true;
    }
    /**
     * Продолжение удаления заказа. Диалог с пользователем<br><br>
     * 1)Получает контекст пользователя и заказ, удаляет заказ в бд<br>
     * Удаляет контекст пользователя и меняет статус заказа так как создание закончилось
     *
     * @param userId id пользователя, который удаляет заказ
     * @param text id заказа
     * @return сообщение об успешном удалении заказа
     */
    public String continueSession(long userId, String text){
        try {
            UserContext userContext = userContextRepository.getUserContext(userId);
            if (userContext.getStateNum() == 0) {
                if(!validation(userId,text))
                    return "Заказ не найден. Попробуй еще раз";

                long idOrder = Long.parseLong(text);
                Order order = orderRepository.getById(idOrder);
                System.out.println(order.getStatus().toString());
                if (order.getStatus() == OrderStatus.UPDATING){
                    System.out.println("_____");
                    userNotifier.sendTextMessage(
                            order.getCourierId(),
                            "Заказчик отменил заказ %s %s".formatted(order.getId(),order.getDescription())
                    );
                }
                orderRepository.delete(order.getId());
                userContextRepository.updateUserContext(userId,new UserContext());
                return "Заказ удален";
            } else
                return "Выход за пределы контекста";
        } catch (SQLException | ParseException | DBException e) {
            return "что-то пошло не так";
        }
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

    public void setUserNotifier(UserNotifier userNotifier) {
        this.userNotifier = userNotifier;
    }
}