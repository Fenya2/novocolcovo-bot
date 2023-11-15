package core.service_handlers.services;

import core.UserNotifier;
import db.OrderRepository;
import db.UserContextRepository;
import models.Order;
import models.OrderStatus;
import models.UserContext;
import models.UserState;
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

    /**
     * Тестирует случаи: <br>
     * 1)Невалидный контекст
     * 2)Невалидный номер заказа
     * 3)Заказа не существует
     * 4)Заказ принадлежит курьеру
     * 5)Все отработало как надо
     */
    @Test
    public void continueSession() throws SQLException, ParseException {
        Mockito.when(userContextRepository.getUserContext(1))
                .thenReturn(
                        new UserContext(UserState.ORDER_ACCEPTING,1)
                );
        String continueSession0 = acceptOrderService.continueSession(1,"1");
        Assert.assertEquals("Выход за пределы контекста",continueSession0);

        Mockito.when(userContextRepository.getUserContext(1))
                .thenReturn(
                        new UserContext(UserState.ORDER_ACCEPTING,0)
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
                        new UserContext(UserState.ORDER_ACCEPTING,0)
                );
        Mockito.when(orderRepository.getById(11))
                .thenReturn(new Order(1));

        String continueSession4 = acceptOrderService.continueSession(2,"11");
        Assert.assertEquals("В этот момент заказ изменяется.",continueSession4);

        Mockito.when(orderRepository.getById(11))
                .thenReturn(new Order(1,1, OrderStatus.PENDING,"any"));

        String continueSession5 = acceptOrderService.continueSession(2,"11");
        Assert.assertEquals("Заказ принят",continueSession5);
    }
}