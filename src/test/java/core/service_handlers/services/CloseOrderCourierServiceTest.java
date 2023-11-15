package core.service_handlers.services;

import core.UserNotifier;
import db.OrderRepository;
import db.UserContextRepository;
import models.Order;
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

/**Класс тестирующий {@link CloseOrderCourierService CloseOrderCourierService}*/
public class CloseOrderCourierServiceTest {
    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }
    @InjectMocks
    private CloseOrderCourierService closeOrderCourierService;
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
     * 5)Заказчик в другом контексте
     * 6)Заказ отправлен на завершение заказчику
     */
    @Test
    public void continueSession() throws SQLException, ParseException {
        UserNotifier userNotifier = Mockito.mock(UserNotifier.class);
        closeOrderCourierService.setUserNotifier(userNotifier);
        Mockito.when(userContextRepository.getUserContext(1))
                .thenReturn(
                        new UserContext(UserState.ORDER_CLOSING_COURIER,1)
                );
        String continueSession0 = closeOrderCourierService.continueSession(1,"1");
        Assert.assertEquals("Выход за пределы контекста",continueSession0);

        Mockito.when(userContextRepository.getUserContext(1))
                .thenReturn(
                        new UserContext(UserState.ORDER_CLOSING_COURIER,0)
                );
        String continueSession1 = closeOrderCourierService.continueSession(
                1,"2k3gokgh9kewokec"
        );
        Assert.assertEquals(
                "Заказ не найден. Попробуй еще раз",continueSession1
        );

        Mockito.when(orderRepository.getById(1))
                .thenReturn(null);
        String continueSession2 = closeOrderCourierService.continueSession(1,"111");
        Assert.assertEquals("Заказ не найден. Попробуй еще раз",continueSession2);

        Mockito.when(orderRepository.getById(1))
                .thenReturn(new Order(1));
        String continueSession3 = closeOrderCourierService.continueSession(1,"111");
        Assert.assertEquals("Заказ не найден. Попробуй еще раз",continueSession3);

        Mockito.when(orderRepository.getById(11))
                .thenReturn(new Order(1));
        Mockito.when(userContextRepository.getUserContext(2))
                .thenReturn(
                        new UserContext(UserState.ORDER_CLOSING_COURIER,0)
                );
        String continueSession4 = closeOrderCourierService.continueSession(2,"11");
        Assert.assertEquals(
                "Извини, но сейчас заказ нельзя принять."
                ,continueSession4
        );

        Order order = new Order(1);
        Mockito.when(orderRepository.getById(11))
                .thenReturn(order);
        Mockito.when(userContextRepository.getUserContext(order.getCreatorId()))
                .thenReturn(new UserContext());
        String continueSession5 = closeOrderCourierService.continueSession(2,"11");
        Assert.assertEquals(
                "Завершение заказа отправлено на подтверждение заказчику"
                ,continueSession5
        );

    }
}