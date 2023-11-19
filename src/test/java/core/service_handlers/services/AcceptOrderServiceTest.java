package core.service_handlers.services;

import core.UserNotifier;
import db.DBException;
import db.OrderRepository;
import db.UserContextRepository;
import db.UserRepository;
import models.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.text.ParseException;


/**Класс тестирующий {@link AcceptOrderService AcceptOrderService}*/
public class AcceptOrderServiceTest {
    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }
    @InjectMocks
    private AcceptOrderService acceptOrderService;
    @Mock
    private UserContextRepository userContextRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;

    /**
     * Тестирует случаи: <br>
     * 1)Невалидный номер заказа
     * 2)Заказа не существует
     * 3)Заказ принадлежит курьеру
     * 4)Все отработало как надо
     */
    @Test
    public void continueSession() throws SQLException, ParseException, DBException {
        Mockito.when(userContextRepository.getUserContext(1))
                .thenReturn(
                        new UserContext(UserState.ORDER_ACCEPT,0)
                );
        String continueSession1 = acceptOrderService.continueSession(
                1,"2k3gokgh9kewokec"
        );
        Assert.assertEquals(
                "Заказ не найден. Попробуй еще раз",continueSession1
        );

        Mockito.when(orderRepository.getById(1))
                .thenReturn(null);
        String continueSession2 = acceptOrderService.continueSession(1,"111");
        Assert.assertEquals("Заказ не найден. Попробуй еще раз",continueSession2);

        Mockito.when(orderRepository.getById(1))
                .thenReturn(new Order(1));
        String continueSession3 = acceptOrderService.continueSession(1,"111");
        Assert.assertEquals("Заказ не найден. Попробуй еще раз",continueSession3);


        UserNotifier userNotifier = Mockito.mock(UserNotifier.class);
        acceptOrderService.setUserNotifier(userNotifier);
        Mockito.when(userContextRepository.getUserContext(2))
                .thenReturn(
                        new UserContext(UserState.ORDER_ACCEPT,0)
                );
        Mockito.when(orderRepository.getById(11))
                .thenReturn(new Order(1));
        Mockito.when(userRepository.getById(1))
                .thenReturn(new User());

        String continueSession4 = acceptOrderService.continueSession(2,"11");
        Assert.assertEquals("Заказ номер -42\n" +
                "Создан: default name\n" +
                "Описание заказчика: default description\n" +
                "Описание заказа: default description\n" +
                "Введите еще раз номер заказа, для подтверждения принятия заказа.Либо команду" +
                " /cancel для выхода из контекста принятия заказа.",continueSession4);

        Mockito.when(orderRepository.getById(11))
                .thenReturn(new Order(1,1, OrderStatus.PENDING,"any"));
        Mockito.when(userContextRepository.getUserContext(1))
                .thenReturn(new UserContext(UserState.NO_STATE));

        Mockito.when(userRepository.getById(2))
                .thenReturn(new User());
        String continueSession5 = acceptOrderService.continueSession(2,"11");
        Assert.assertEquals("Заказ принят. Контакты для связи с заказчиком:\n" +
                "\n" +
                "TELEGRAM: null\n" +
                "VK: null",continueSession5);
    }
}