package core;

import db.*;
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
    private OrderRepository orderRepository;
    @Mock
    private UserContextRepository userContextRepository;
    /**
     * Проверяет случай когда пользователь вводит команду /register и не авторизован.
     */
    @Test
    public void registerWhenUserNotAutorized() {
        Message msg = new Message();
        msg.setUser(null); // важно
        System.out.println(msg.getUser());
        msg.setText("/register");
        Assert.assertEquals("""
            Готово! Аккаунт создан.
            Вы находитесь в меню редактирования пользователя.
            Введитете /help для справки.
            """, serviceManager.register(msg));
    }

    /**
     * Проверяет случай когда пользователь вводит команду /register и авторизован.
     */
    @Test
    public void registerWhenUserAutorized() {
        Message msg = new Message();
        msg.setUser(Mockito.mock(User.class)); // важно
        msg.setText("/register");
        Assert.assertEquals("Вы уже зарегистрированы.", serviceManager.register(msg));
    }

    /** Проверяет работу startCreateOrder */
    @Test
    public void startCreateOrder() throws SQLException, DBException {
        User user = new User();
        Mockito.when(orderRepository.save(Mockito.any()))
                .thenReturn(new Order());
        String startCreateOrder = serviceManager.startCreateOrder(user.getId());
        Assert.assertEquals("Введите список продуктов", startCreateOrder);
    }

    /**
     * Проверяет работу startEditOrder,
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
        Assert.assertEquals("У вас нет ни одного заказа", startEditOrder1);

        String startEditOrder2 = serviceManager.startEditOrder(1);
        Assert.assertEquals("Какой заказ вы хотите обновить.?\n-42: \n", startEditOrder2);

    }

    /**
     * Проверяет работу startCancelOrder,
     * рассматривает случаи когда нет ни одного заказа
     * и когда список заказов не пустой
     */
    @Test
    public void testStartCancelOrder() throws SQLException, ParseException {
        ArrayList<Order> listAllOrder = new ArrayList<>(1);
        Order order = new Order(1);
        order.setDescription("");
        listAllOrder.add(order);
        Mockito.when(orderRepository.getAll())
                .thenReturn(listAllOrder);
        String startEditOrder1 = serviceManager.startCancelOrder(-1);
        Assert.assertEquals("У вас нет ни одного заказа", startEditOrder1);

        String startEditOrder2 = serviceManager.startCancelOrder(1);
        Assert.assertEquals("Какой заказ вы хотите удалить.?\n-42: \n", startEditOrder2);

    }
    /** Проверяет работу команды show_order в случае, когда нет ни одного заказа.
     * В случае когда у пользователя имеется хотя бы один заказ.
     */
    @Test
    public void testShowOrder() throws SQLException, ParseException {
        ArrayList<Order> listAllOrder = new ArrayList<>(1);
        Order order = new Order(1);
        order.setDescription("");
        listAllOrder.add(order);
        Mockito.when(orderRepository.getAll())
                .thenReturn(listAllOrder);
        String showOrder1 = serviceManager.showOrder(-1);
        Assert.assertEquals("У вас нет ни одного заказа", showOrder1);

        String showOrder2 = serviceManager.showOrder(1);
        Assert.assertEquals("-42: \n", showOrder2);
    }

    /**
     * Проверяет команду /showPendingOrders, случаи <br>
     * 1)вывод всех заказов (у курьера нет заказов)<br>
     * 2)вывод всех заказов, кроме заказов курьера<br>
     * 3)вывод сообщений об отсутствии заказов
     */
    @Test
    public void showPendingOrders() throws SQLException, ParseException {
        Order aa1 = new Order(1,0,OrderStatus.PENDING,"заказ1");
        Order aa2 = new Order(2,0,OrderStatus.PENDING,"заказ2");
        Order aa3 = new Order(3,0,OrderStatus.UPDATING,"заказ3");
        ArrayList<Order> a = new ArrayList<>();
        a.add(aa1);
        a.add(aa2);
        a.add(aa3);
        Mockito.when(orderRepository.getAll()).
                thenReturn(a);
        String showPendingOrders1 = serviceManager.showPendingOrders(3);
        Assert.assertEquals("-42: заказ1\n-42: заказ2\n",showPendingOrders1);

        String showPendingOrders2 = serviceManager.showPendingOrders(1);
        Assert.assertEquals("-42: заказ2\n",showPendingOrders2);

        a.remove(1);
        String showPendingOrders3 = serviceManager.showPendingOrders(1);
        Assert.assertEquals("У вас нет ни одного заказа", showPendingOrders3);
    }

    /**
     * Проверяет команду /startAcceptOrder, случаи <br>
     * 1) можно принять хотя бы 1 заказ
     * 2) нет ни одного заказа для принятия
     */
    @Test
    public void startAcceptOrder() throws SQLException, ParseException {
        Order aa1 = new Order(1,0,OrderStatus.PENDING,"заказ1");
        ArrayList<Order> a = new ArrayList<>();
        a.add(aa1);
        Mockito.when(orderRepository.getAll()).
                thenReturn(a);
        String startCloseOrder1 = serviceManager.startAcceptOrder(3);
        Assert.assertEquals("Введите заказ который хотите принять",startCloseOrder1);

        String startCloseOrder2 = serviceManager.startAcceptOrder(1);
        Assert.assertEquals("У вас нет ни одного заказа",startCloseOrder2);

    }

    /**
     * Проверяет команду /showAcceptOrder, случаи <br>
     * 1)вывод всех заказов <br>
     * 2)вывод сообщений об отсутствии заказов
     */
    @Test
    public void showAcceptOrder() throws SQLException, ParseException {
        Order aa1 = new Order(1,3,OrderStatus.RUNNING,"заказ1");
        Order aa2 = new Order(2,3,OrderStatus.RUNNING,"заказ2");
        Order aa3 = new Order(3,1,OrderStatus.UPDATING,"заказ3");
        ArrayList<Order> a = new ArrayList<>();
        a.add(aa1);
        a.add(aa2);
        a.add(aa3);
        Mockito.when(orderRepository.getAll()).
                thenReturn(a);
        String showAcceptOrders1 = serviceManager.showAcceptOrder(3);
        Assert.assertEquals("-42: заказ1\n-42: заказ2\n",showAcceptOrders1);

        String showAcceptOrders2 = serviceManager.showAcceptOrder(1);
        Assert.assertEquals("У вас нет ни одного заказа",showAcceptOrders2);
    }

    /**
     * Проверяет команду /startCloseOrder, случаи <br>
     * 1) можно завершить хотя бы 1 заказ
     * 2) нет ни одного заказа для завершения
     */
    @Test
    public void startCloseOrder() throws SQLException, ParseException {
        Order aa1 = new Order(1,3,OrderStatus.RUNNING,"заказ1");
        ArrayList<Order> a = new ArrayList<>();
        a.add(aa1);
        Mockito.when(orderRepository.getAll()).
                thenReturn(a);
        String startCloseOrder1 = serviceManager.startCloseOrder(3);
        Assert.assertEquals("Введите заказ который хотите завершить",startCloseOrder1);

        String startCloseOrder2 = serviceManager.startCloseOrder(1);
        Assert.assertEquals("У вас нет ни одного заказа",startCloseOrder2);
    }
}