package core.service_handlers.services;

import core.UserNotifier;
import db.*;
import models.*;

import java.sql.SQLException;
import java.text.ParseException;

/** Сервис для работы с контекстом {@link models.UserState#ORDER_ACCEPT ORDER_ACCEPTING}*/
public class AcceptOrderService {

    /** @see UserNotifier */
    private UserNotifier userNotifier;

    /** @see OrderRepository */
    private final OrderRepository orderRepository;

    /** @see UserContextRepository */
    private final UserContextRepository userContextRepository;

    /** @see db.UserRepository */
    private final UserRepository userRepository;

    /** @see db.LoggedUsersRepository */
    private final LoggedUsersRepository loggedUsersRepository;

    /** Конструктор {@link AcceptOrderService}*/
    public AcceptOrderService(OrderRepository orderRepository, UserContextRepository userContextRepository, UserRepository userRepository, LoggedUsersRepository loggedUsersRepository) {
        this.orderRepository = orderRepository;
        this.userContextRepository = userContextRepository;
        this.userRepository = userRepository;
        this.loggedUsersRepository = loggedUsersRepository;
    }

    /**
     * Меняет у введенного курьером заказа статус на {@link OrderStatus#RUNNING RUNNING} <br>
     * Меняет поле {@link Order#courierId  courierId} на id курьера <br>
     * Меняет контекст курьера на {@link UserState#NO_STATE NO_STATE}
     * Отправляет сообщение, что заказ принят или ошибку
     * так же отправляет сообщение заказчику, что его заказ хотят принять
     * @param userId id курьера
     * @param text id заказа
     */
    public String continueSession(long userId, String text) {
        try {
            if(!validation(userId,text))
                return "Заказ не найден. Попробуй еще раз";

            long idOrder = Long.parseLong(text);
            Order order = orderRepository.getById(idOrder);
            UserContext userContextCourier = userContextRepository.getUserContext(userId);
            switch (userContextCourier.getStateNum()){
                case 0->{
                    userContextCourier.incrementStateNum();
                    userContextRepository.updateUserContext(userId,userContextCourier);
                    User client = userRepository.getById(order.getCreatorId());
                    String str = String.format(
                            "Заказ номер %s\n" +
                            "Создан: %s\n" +
                            "Описание заказчика: %s\n" +
                            "Описание заказа: %s\n" +
                            "Введите еще раз номер заказа, для подтверждения принятия заказа." +
                            "Либо команду /cancel для выхода из контекста принятия заказа.",
                            order.getId(), client.getName(), client.getDescription(), order.getDescription());
                    return str;
                }
                case 1->{
                    UserContext userContextClient = userContextRepository.getUserContext(order.getCreatorId());
                    if(userContextClient.getState() != UserState.NO_STATE){
                        userNotifier.sendTextMessage(
                                order.getCreatorId(),
                                "Курьер хочет принять заказ, заверши выполнение команды."
                        );
                        return "Извини, но сейчас заказ нельзя принять.";
                    }

                    order.setCourierId(userId);
                    order.setStatus(OrderStatus.RUNNING);
                    orderRepository.update(order);
                    userContextRepository.updateUserContext(userId, new UserContext());



                    User courier = userRepository.getById(userId);
                    String str = "Ваш заказ %s принят курьером (%s %s). Контакты для связи с курьером:\n"
                            .formatted(order.getDescription(),courier.getName(),courier.getDescription());

                    StringBuilder mesClient = new StringBuilder(str);
                    String idOnPlatform;
                    for (Platform platform: Platform.values()){
                        idOnPlatform = loggedUsersRepository.
                                getUserIdOnPlatformByUserIdAndPlatform(order.getCourierId(),platform);
                        if (platform==Platform.NO_PLATFORM) continue;
                        String name = userNotifier.getUserDomainOnPlatform(platform,idOnPlatform);
                        mesClient.append("\n").append(platform).append(": ").append(name);
                    }
                    userNotifier.sendTextMessage(order.getCreatorId(), mesClient.toString());

                    StringBuilder mesCourier = new StringBuilder("Заказ принят. Контакты для связи с заказчиком:\n");
                    for (Platform platform: Platform.values()){
                        idOnPlatform = loggedUsersRepository.
                                getUserIdOnPlatformByUserIdAndPlatform(order.getCreatorId(),platform);
                        if (platform==Platform.NO_PLATFORM) continue;
                        String name = userNotifier.getUserDomainOnPlatform(platform,idOnPlatform);
                        mesCourier.append("\n").append(platform).append(": ").append(name);
                    }
                    return mesCourier.toString();
                }
                default -> {return "Выход за пределы контекста";}
            }
        } catch (SQLException | ParseException | DBException e) {
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

        if (userId==order.getCreatorId())
            return false;
        if(order.getStatus()!=OrderStatus.PENDING)
            return false;
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

    public void setUserNotifier(UserNotifier userNotifier) {
        this.userNotifier = userNotifier;
    }
}
