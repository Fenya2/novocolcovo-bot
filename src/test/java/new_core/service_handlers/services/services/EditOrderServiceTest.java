package new_core.service_handlers.services.services;

import db.OrderRepository;
import db.UserContextRepository;
import models.*;
import new_core.service_handlers.services.EditOrderService;
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

public class EditOrderServiceTest {
    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @InjectMocks
    private EditOrderService editOrderService;
    @Mock
    private UserContextRepository userContextRepository;
    @Mock
    private OrderRepository orderRepository;

    /**
     * Проверяет работу continueEditOrder с контекстом пользователя = 0
     * Проверяет контекст пользователя, если он равен 0, то <br>
     * (1) проверяет на число и на кол-во цифр в числе
     * (2) проверяет на наличие кокретного заказа в системе
     * (3) проверяет на наличие кокретного заказа у пользователя
     * так же проверяет случай когда все условия соблюдены
     */
    @Test
    public void continueSession0() throws SQLException, ParseException {
        UserContext userContext1 = new UserContext(UserState.ORDER_EDITING, 10);
        Mockito.when(userContextRepository.getUserContext(1))
                .thenReturn(userContext1);
        String continueEditOrder = editOrderService.continueSession(1, "no digit");
        Assert.assertEquals("Выход за пределы контекста", continueEditOrder);

        UserContext userContext = new UserContext(UserState.ORDER_EDITING, 0);
        Mockito.when(userContextRepository.getUserContext(1))
                .thenReturn(userContext);
        String continueEditOrder1 = editOrderService.continueSession(1, "no digit");
        Assert.assertEquals("Заказ не найден. Попробуйте еще раз(1)", continueEditOrder1);

        String continueEditOrder2 = editOrderService.continueSession(1, "1234567891234567899");
        Assert.assertEquals("Заказ не найден. Попробуйте еще раз(1)", continueEditOrder2);

        Mockito.when(orderRepository.getById(0))
                .thenReturn(null);
        String continueEditOrder3 = editOrderService.continueSession(1, "123456789");
        Assert.assertEquals("Заказ не найден. Попробуйте еще раз(2)", continueEditOrder3);

        Mockito.when(orderRepository.getById(11254))
                .thenReturn(new Order(1));
        Mockito.when(orderRepository.getOrderByIdUserAndStatus(1, OrderStatus.PENDING))
                .thenReturn(null);
        String continueEditOrder4 = editOrderService.continueSession(1, "11254");
        Assert.assertEquals("Заказ не найден. Попробуйте еще раз(3)", continueEditOrder4);

        Mockito.when(orderRepository.getOrderByIdUserAndStatus(1, OrderStatus.UPDATING))
                .thenReturn(new Order(1));
        String continueEditOrder5 = editOrderService.continueSession(1, "11254");
        Assert.assertEquals("Напишите новый список продуктов", continueEditOrder5);

    }

    /**
     * Проверяет работу continueUdpateOrder с контекстом пользователя = 1
     * проверяет контекст пользователя, если он равен 1
     * проверяет кореектность работы метода, иначе проверяет сообщение об ошибке
     */
    @Test
    public void continueSession1() throws SQLException, ParseException {
        UserContext userContext1 = new UserContext(UserState.ORDER_EDITING, 1);
        Mockito.when(userContextRepository.getUserContext(1))
                .thenReturn(userContext1);
        Mockito.when(orderRepository.getOrderByIdUserAndStatus(1, OrderStatus.UPDATING))
                .thenReturn(new Order());
        String continueEditOrder = editOrderService.continueSession(1, "11254");
        Assert.assertEquals("Заказ изменен", continueEditOrder);
    }
}
