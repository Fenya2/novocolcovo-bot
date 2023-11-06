package core.service_handlers.services;

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

public class CreateOrderServiceTest {
    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @InjectMocks
    private CreateOrderService createOrderService;
    @Mock
    private UserContextRepository userContextRepository;
    @Mock
    private OrderRepository orderRepository;

    /**
     * Проверяет работу continueCreateOrder,
     * рассматривает случаи, когда контекст имеет допустимое значение
     * и когда контекст выходит за границы допустимых значений
     */
    @Test
    public void testContinueSession() throws SQLException, ParseException {
        User user = new User();
        UserContext context1 = new UserContext(UserState.ORDER_CREATING, 10);
        Mockito.when(userContextRepository.getUserContext(user.getId()))
                .thenReturn(context1);
        String continueCreateOrder1 = createOrderService.continueSession(user.getId(), "anyText");
        Assert.assertEquals("Выход за пределы контекста", continueCreateOrder1);

        UserContext context2 = new UserContext(UserState.ORDER_CREATING, 0);
        Mockito.when(userContextRepository.getUserContext(user.getId()))
                .thenReturn(context2);
        Mockito.when(orderRepository.getOrderByIdUserAndStatus(user.getId(), OrderStatus.UPDATING))
                .thenReturn(new Order());
        String continueCreateOrder2 = createOrderService.continueSession(user.getId(), "anyText");
        Assert.assertEquals("Заказ создан", continueCreateOrder2);

    }

    @Test
    public void cancel() {
    }
}