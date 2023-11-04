package core.service_handlers.services.services;

import db.LoggedUsersRepository;
import db.OrderRepository;
import db.UserContextRepository;
import db.UserRepository;
import models.*;
import core.service_handlers.services.ServiceManager;
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

public class ServiceManagerTest {
    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @InjectMocks
    private ServiceManager serviceManager;

    @Mock
    private LoggedUsersRepository loggedUsersRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserContextRepository userContextRepository;
    @Mock
    private OrderRepository orderRepository;
    /**
     * Проверяет случай когда пользователь вводит команду /start.
     */
    @Test
    public void start() throws SQLException {
        Message msg = new Message();
        msg.setText("/start");
        Mockito.when(loggedUsersRepository.getUserByPlatformAndIdOnPlatform(Mockito.any(),Mockito.any()))
                .thenReturn(null);
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(new User());
        Assert.assertEquals("Привет. Напишите /help",serviceManager.start(msg));

        Mockito.when(loggedUsersRepository.getUserByPlatformAndIdOnPlatform(Mockito.any(),Mockito.any()))
                .thenReturn(new User());
        Assert.assertEquals("Привет. Напишите /help",serviceManager.start(msg));
    }
    /** Проверяет работу startCreateOrder */
    @Test
    public void startCreateOrder() throws SQLException {
        User user = new User();
        Mockito.when(orderRepository.save(Mockito.any()))
                .thenReturn(new Order());
        String startCreateOrder = serviceManager.startCreateOrder(user.getId());
        Assert.assertEquals("Введите список продуктов", startCreateOrder);
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
        String startEditOrder1 = serviceManager.startEditOrder(-1);
        Assert.assertEquals("у вас нет ни одного заказа", startEditOrder1);

        String startEditOrder2 = serviceManager.startEditOrder(1);
        Assert.assertEquals("Какой заказ вы хотите обновить.?\n-42: \n", startEditOrder2);

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
        String startEditOrder1 = serviceManager.startCancelOrder(-1);
        Assert.assertEquals("у вас нет ни одного заказа", startEditOrder1);

        String startEditOrder2 = serviceManager.startCancelOrder(1);
        Assert.assertEquals("Какой заказ вы хотите удалить.?\n-42: \n", startEditOrder2);

    }
    /** Проверяет работу команды show_order в случае, когда нет ни одного заказа.
     * В случае когда у пользователя имеется хотя бы один заказ.
     */
    @Test
    public void showOrder() throws SQLException, ParseException {
        ArrayList<Order> listAllOrder = new ArrayList<>(1);
        Order order = new Order(1);
        order.setDescription("");
        listAllOrder.add(order);
        Mockito.when(orderRepository.getAll())
                .thenReturn(listAllOrder);
        String showOrder1 = serviceManager.showOrder(-1);
        Assert.assertEquals("у вас нет ни одного заказа", showOrder1);

        String showOrder2 = serviceManager.showOrder(1);
        Assert.assertEquals("-42: \n", showOrder2);
    }
}