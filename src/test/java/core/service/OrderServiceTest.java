package core.service;

import db.OrderRepository;
import db.UserContextRepository;
import models.Order;
import models.User;
import models.UserContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

public class OrderServiceTest {
    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @InjectMocks
    private OrderService orderService;
    @Mock
    private UserContextRepository userContextRepository;
    @Mock
    private OrderRepository orderRepository;

    /** Проверяет работу startCreateOrder */
    @Test
    public void startCreateOrder() throws SQLException {
        User user = new User();
        Mockito.when(orderRepository.save(Mockito.any()))
                .thenReturn(new Order());
        String startCreateOrder = orderService.startCreateOrder(user.getId());
        Assert.assertEquals("Введите список продуктов", startCreateOrder);
    }

    /**
     * Проверяет работу continueCreateOrder
     * рассматривает случаи, когда конекст имеет допустимое значени
     * и когда контекст выходит за границы допустимых значений
     */
    @Test
    public void continueCreateOrder() throws SQLException, ParseException {
        User user = new User();
        UserContext context1 = new UserContext("create_order", 10);
        Mockito.when(userContextRepository.getUserContext(user.getId()))
                .thenReturn(context1);
        String continueCreateOrder1 = orderService.continueCreateOrder(user.getId(), "anyText");
        Assert.assertEquals("Выход за пределы контекста", continueCreateOrder1);

        UserContext context2 = new UserContext("create_order", 0);
        Mockito.when(userContextRepository.getUserContext(user.getId()))
                .thenReturn(context2);
        Mockito.when(orderRepository.getOrderByIdUserAndStatus(user.getId(), "updating"))
                .thenReturn(new Order());
        String continueCreateOrder2 = orderService.continueCreateOrder(user.getId(), "anyText");
        Assert.assertEquals("Заказ создан", continueCreateOrder2);
    }

    /**
     * Проверяет работу startEditOrder
     * рассматривает случаи когда нет ни одного заказа
     * и когда список заказов не пустой
     */
    @Test
    public void startEditOrder() throws SQLException, ParseException {
        ArrayList<Order> listAllOrder = new ArrayList<>(1);
        Order order = new Order(1);
        order.setDescription("");
        listAllOrder.add(order);
        Mockito.when(orderRepository.getAll())
                .thenReturn(listAllOrder);
        String startEditOrder1 = orderService.startEditOrder(-1);
        Assert.assertEquals("у вас нет ни одного заказа", startEditOrder1);

        String startEditOrder2 = orderService.startEditOrder(1);
        Assert.assertEquals("Какой заказ вы хотите обновить.?\n-42: \n", startEditOrder2);
    }

    /**
     * Проверяет работу continueEditOrder с контекстом пользователя = 0
     * Проверяет контекст пользователя, если он равен 0, то <br>
     * (1) проверяет на число и на кол-во цифр в числе
     * (2) проверяет на наличие кокретного заказа в системе
     * (3) проверяет на наличие кокретного заказа у пользователя
     * так же проверяет случай когда все условия соблюдены
     */
    @Test
    public void continueEditOrder0() throws SQLException, ParseException {
        UserContext userContext1 = new UserContext("edit_order", 10);
        Mockito.when(userContextRepository.getUserContext(1))
                .thenReturn(userContext1);
        String continueEditOrder = orderService.continueEditOrder(1, "no digit");
        Assert.assertEquals("Выход за пределы контекста", continueEditOrder);

        UserContext userContext = new UserContext("edit_order", 0);
        Mockito.when(userContextRepository.getUserContext(1))
                .thenReturn(userContext);
        String continueEditOrder1 = orderService.continueEditOrder(1, "no digit");
        Assert.assertEquals("Заказ не найден. Попробуйте еще раз(1)", continueEditOrder1);

        String continueEditOrder2 = orderService.continueEditOrder(1, "1234567891234567899");
        Assert.assertEquals("Заказ не найден. Попробуйте еще раз(1)", continueEditOrder2);

        Mockito.when(orderRepository.getById(0))
                .thenReturn(null);
        String continueEditOrder3 = orderService.continueEditOrder(1, "123456789");
        Assert.assertEquals("Заказ не найден. Попробуйте еще раз(2)", continueEditOrder3);

        Mockito.when(orderRepository.getById(11254))
                .thenReturn(new Order(1));
        Mockito.when(orderRepository.getOrderByIdUserAndStatus(1, "pending"))
                .thenReturn(null);
        String continueEditOrder4 = orderService.continueEditOrder(1, "11254");
        Assert.assertEquals("Заказ не найден. Попробуйте еще раз(3)", continueEditOrder4);

        Mockito.when(orderRepository.getOrderByIdUserAndStatus(1, "updating"))
                .thenReturn(new Order(1));
        String continueEditOrder5 = orderService.continueEditOrder(1, "11254");
        Assert.assertEquals("Напишите новый список продуктов", continueEditOrder5);

    }

    /**
     * Проверяет работу continueUdpateOrder с контекстом пользователя = 1
     * проверяет контекст пользователя, если он равен 1
     * проверяет кореектность работы метода, иначе проверяет сообщение об ошибке
     */
    @Test
    public void continueEditOrder1() throws SQLException, ParseException {
        UserContext userContext1 = new UserContext("edit_order", 1);
        Mockito.when(userContextRepository.getUserContext(1))
                .thenReturn(userContext1);
        Mockito.when(orderRepository.getOrderByIdUserAndStatus(1, "updating"))
                .thenReturn(new Order());
        String continueEditOrder5 = orderService.continueEditOrder(1, "11254");
        Assert.assertEquals("Заказ изменен", continueEditOrder5);
    }

    /**
     * Проверяет работу startCancelOrder
     * рассматривает случаи когда нет ни одного заказа
     * и когда список заказов не пустой
     */
    @Test
    public void startCancelOrder() throws SQLException, ParseException {
        ArrayList<Order> listAllOrder = new ArrayList<>(1);
        Order order = new Order(1);
        order.setDescription("");
        listAllOrder.add(order);
        Mockito.when(orderRepository.getAll())
                .thenReturn(listAllOrder);
        String startEditOrder1 = orderService.startCancelOrder(-1);
        Assert.assertEquals("у вас нет ни одного заказа", startEditOrder1);

        String startEditOrder2 = orderService.startCancelOrder(1);
        Assert.assertEquals("Какой заказ вы хотите удалить.?\n-42: \n", startEditOrder2);
    }

    /**
     * Проверяет работу continueCancelOrder
     * Проверяет контекст пользователя, если он равен 0, то <br>
     * (1) проверяет на число и на кол-во цифр в числе <br>
     * (2) проверяет на наличие кокретного заказа в системе <br>
     * (3) проверяет на наличие кокретного заказа у пользователя <br>
     * так же проверяет случай когда все условия соблюдены
     */
    @Test
    public void continueCancelOrder() throws SQLException, ParseException {
        UserContext userContext1 = new UserContext("edit_order", 10);
        Mockito.when(userContextRepository.getUserContext(1))
                .thenReturn(userContext1);
        String continueEditOrder = orderService.continueCancelOrder(1, "no digit");
        Assert.assertEquals("Выход за пределы контекста", continueEditOrder);


        UserContext userContext = new UserContext("cancel_order", 0);
        Mockito.when(userContextRepository.getUserContext(1))
                .thenReturn(userContext);

        String continueCancelOrder1 = orderService.continueCancelOrder(1, "no digit");
        Assert.assertEquals("Заказ не найден. Попробуйте еще раз(1)", continueCancelOrder1);

        String continueCancelOrder2 = orderService.continueCancelOrder(1, "1234567891234567899");
        Assert.assertEquals("Заказ не найден. Попробуйте еще раз(1)", continueCancelOrder2);

        Mockito.when(orderRepository.getById(0))
                .thenReturn(null);
        String continueCancelOrder3 = orderService.continueCancelOrder(1, "123456789");
        Assert.assertEquals("Заказ не найден. Попробуйте еще раз(2)", continueCancelOrder3);

        Mockito.when(orderRepository.getById(11254))
                .thenReturn(new Order(1));
        Mockito.when(orderRepository.getOrderByIdUserAndStatus(1, "pending"))
                .thenReturn(null);
        String continueCancelOrder4 = orderService.continueCancelOrder(1, "11254");
        Assert.assertEquals("Заказ не найден. Попробуйте еще раз(3)", continueCancelOrder4);

        Mockito.when(orderRepository.getOrderByIdUserAndStatus(1, "updating"))
                .thenReturn(new Order(1));
        String continueCancelOrder5 = orderService.continueCancelOrder(1, "11254");
        Assert.assertEquals("Заказ удален", continueCancelOrder5);
    }

    @Test
    public void showOrder() throws SQLException, ParseException {
        ArrayList<Order> listAllOrder = new ArrayList<>(1);
        Order order = new Order(1);
        order.setDescription("");
        listAllOrder.add(order);
        Mockito.when(orderRepository.getAll())
                .thenReturn(listAllOrder);
        String showOrder1 = orderService.showOrder(-1);
        Assert.assertEquals("у вас нет ни одного заказа", showOrder1);

        String showOrder2 = orderService.showOrder(1);
        Assert.assertEquals("-42: \n", showOrder2);

    }
}