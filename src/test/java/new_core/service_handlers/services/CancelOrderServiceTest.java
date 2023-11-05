package new_core.service_handlers.services;

import db.OrderRepository;
import db.UserContextRepository;
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

import static org.junit.Assert.*;

public class CancelOrderServiceTest {
    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @InjectMocks
    private CancelOrderService cancelOrderService;
    @Mock
    private UserContextRepository userContextRepository;
    @Mock
    private OrderRepository orderRepository;

    /**
     * Проверяет работу continueCreateOrder,
     * рассматривает случаи, когда конекст имеет допустимое значени
     * и когда контекст выходит за границы допустимых значений
     */
    @Test
    public void continueSession() throws SQLException, ParseException {
        UserContext userContext1 = new UserContext(UserState.ORDER_EDITING, 10);
        Mockito.when(userContextRepository.getUserContext(1))
                .thenReturn(userContext1);
        String continueEditOrder = cancelOrderService.continueSession(1, "no digit");
        Assert.assertEquals("Выход за пределы контекста", continueEditOrder);


        UserContext userContext = new UserContext(UserState.ORDER_CANCELING, 0);
        Mockito.when(userContextRepository.getUserContext(1))
                .thenReturn(userContext);

        String continueCancelOrder1 = cancelOrderService.continueSession(1, "no digit");
        Assert.assertEquals("Заказ не найден. Попробуйте еще раз", continueCancelOrder1);

        String continueCancelOrder2 = cancelOrderService.continueSession(1, "1234567891234567899");
        Assert.assertEquals("Заказ не найден. Попробуйте еще раз", continueCancelOrder2);

        Mockito.when(orderRepository.getById(0))
                .thenReturn(null);
        String continueCancelOrder3 = cancelOrderService.continueSession(1, "123456789");
        Assert.assertEquals("Заказ не найден. Попробуйте еще раз", continueCancelOrder3);

        Mockito.when(orderRepository.getById(11254))
                .thenReturn(new Order(1));
        Mockito.when(orderRepository.getOrderByIdUserAndStatus(1, OrderStatus.PENDING))
                .thenReturn(null);
        String continueCancelOrder4 = cancelOrderService.continueSession(1, "11254");
        Assert.assertEquals("Заказ не найден. Попробуйте еще раз", continueCancelOrder4);

        Mockito.when(orderRepository.getOrderByIdUserAndStatus(1, OrderStatus.UPDATING))
                .thenReturn(new Order(1));
        String continueCancelOrder5 = cancelOrderService.continueSession(1, "11254");
        Assert.assertEquals("Заказ удален", continueCancelOrder5);
    }
}