package core.service;

import db.OrderRepository;
import db.UserContextRepository;
import models.Order;
import models.User;
import models.UserContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {
    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }
    @InjectMocks
    private OrderService orderService;
    @Mock
    private UserContextRepository userContextRepository;
    @Mock
    private OrderRepository orderRepository;
    @Test
    void continueCreateOrder() throws SQLException, ParseException {
        User user = new User();
        UserContext context1= new UserContext("create_order",10);
        Mockito.when(userContextRepository.getUserContext(user.getId()))
                .thenReturn(context1);

        String continueCreateOrder1 = orderService.continueCreateOrder(user.getId(),"anyText");
        Assertions.assertEquals("Выход за пределы контекста",continueCreateOrder1);
        Order order = new Order(1);
        order.setDescription("1");
        UserContext context2= new UserContext("create_order",0);
        Mockito.when(userContextRepository.getUserContext(user.getId()))
                .thenReturn(context2);
        Mockito.when(orderRepository.getOrderByIdUserAndStatus(user.getId(),"updating"))
                .thenReturn(order);
        String continueCreateOrder2 = orderService.continueCreateOrder(user.getId(),"anyText");
        Assertions.assertEquals("Заказ создан",continueCreateOrder2);
    }

    @Test
    void startUpdateOrder() throws SQLException, ParseException {
        ArrayList<Order> listAllOrder = new ArrayList<>(1);
        Order order = new Order(1);
        order.setDescription("");
        listAllOrder.add(order);
        Mockito.when(orderRepository.getAll())
                .thenReturn(listAllOrder);
        String startUpdateOrder1 = orderService.startUpdateOrder(-1);
        Assertions.assertEquals(startUpdateOrder1,"у вас нет ни одного заказа");

        String startUpdateOrder2 = orderService.startUpdateOrder(1);
        Assertions.assertEquals(startUpdateOrder2,"Какой заказ вы хотите обновить.?\n-42: \n");
    }

    @Test
    void continueUpdateOrder0() throws SQLException, ParseException {
        UserContext userContext1 = new UserContext("update_order",10);
        Mockito.when(userContextRepository.getUserContext(1))
                .thenReturn(userContext1);
        String continueUpdateOrder = orderService.continueUpdateOrder(1,"no digit");
        Assertions.assertEquals(continueUpdateOrder,"Выход за пределы контекста");

        UserContext userContext = new UserContext("update_order",0);
        Mockito.when(userContextRepository.getUserContext(1))
                .thenReturn(userContext);

        String continueUpdateOrder1 = orderService.continueUpdateOrder(1,"no digit");
        Assertions.assertEquals(continueUpdateOrder1,"Заказ не найден. Попробуйте еще раз(1)");

        String continueUpdateOrder2 = orderService.continueUpdateOrder(1,"1234567891234567899");
        Assertions.assertEquals(continueUpdateOrder2,"Заказ не найден. Попробуйте еще раз(1)");

        Mockito.when(orderRepository.getById(0))
                .thenReturn(null);
        String continueUpdateOrder3 = orderService.continueUpdateOrder(1,"123456789");
        Assertions.assertEquals(continueUpdateOrder3,"Заказ не найден. Попробуйте еще раз(2)");

        Mockito.when(orderRepository.getById(11254))
                .thenReturn(new Order(1));
        Mockito.when(orderRepository.getOrderByIdUserAndStatus(1,"pending"))
                .thenReturn(null);
        String continueUpdateOrder4 = orderService.continueUpdateOrder(1,"11254");
        Assertions.assertEquals(continueUpdateOrder4,"Заказ не найден. Попробуйте еще раз(3)");

        Mockito.when(orderRepository.getOrderByIdUserAndStatus(1,"updating"))
                .thenReturn(new Order(1));
        String continueUpdateOrder5 = orderService.continueUpdateOrder(1,"11254");
        Assertions.assertEquals(continueUpdateOrder5,"Напишите новый список продуктов");

    }
    @Test
    void continueUpdateOrder1() throws SQLException, ParseException {
        UserContext userContext1 = new UserContext("update_order",1);
        Mockito.when(userContextRepository.getUserContext(1))
                .thenReturn(userContext1);
        Mockito.when(orderRepository.getOrderByIdUserAndStatus(1,"updating"))
                .thenReturn(new Order());
        String continueUpdateOrder5 = orderService.continueUpdateOrder(1,"11254");
        Assertions.assertEquals(continueUpdateOrder5,"Заказ изменен");
    }

    @Test
    void startСancelOrder() throws SQLException, ParseException {
        ArrayList<Order> listAllOrder = new ArrayList<>(1);
        Order order = new Order(1);
        order.setDescription("");
        listAllOrder.add(order);
        Mockito.when(orderRepository.getAll())
                .thenReturn(listAllOrder);
        String startUpdateOrder1 = orderService.startСancelOrder(-1);
        Assertions.assertEquals(startUpdateOrder1,"у вас нет ни одного заказа");

        String startUpdateOrder2 = orderService.startСancelOrder(1);
        Assertions.assertEquals(startUpdateOrder2,"Какой заказ вы хотите удалить.?\n-42: \n");
    }

    @Test
    void continueСancelOrder() throws SQLException, ParseException {
        UserContext userContext1 = new UserContext("update_order",10);
        Mockito.when(userContextRepository.getUserContext(1))
                .thenReturn(userContext1);
        String continueUpdateOrder = orderService.continueСancelOrder(1,"no digit");
        Assertions.assertEquals(continueUpdateOrder,"Выход за пределы контекста");


        UserContext userContext = new UserContext("cancel_order",0);
        Mockito.when(userContextRepository.getUserContext(1))
                .thenReturn(userContext);

        String continueСancelOrder1 = orderService.continueСancelOrder(1,"no digit");
        Assertions.assertEquals(continueСancelOrder1,"Заказ не найден. Попробуйте еще раз(1)");

        String continueСancelOrder2 = orderService.continueСancelOrder(1,"1234567891234567899");
        Assertions.assertEquals(continueСancelOrder2,"Заказ не найден. Попробуйте еще раз(1)");

        Mockito.when(orderRepository.getById(0))
                .thenReturn(null);
        String continueСancelOrder3 = orderService.continueСancelOrder(1,"123456789");
        Assertions.assertEquals(continueСancelOrder3,"Заказ не найден. Попробуйте еще раз(2)");

        Mockito.when(orderRepository.getById(11254))
                .thenReturn(new Order(1));
        Mockito.when(orderRepository.getOrderByIdUserAndStatus(1,"pending"))
                .thenReturn(null);
        String continueСancelOrder4 = orderService.continueСancelOrder(1,"11254");
        Assertions.assertEquals(continueСancelOrder4,"Заказ не найден. Попробуйте еще раз(3)");

        Mockito.when(orderRepository.getOrderByIdUserAndStatus(1,"updating"))
                .thenReturn(new Order(1));
        String continueСancelOrder5 = orderService.continueСancelOrder(1,"11254");
        Assertions.assertEquals(continueСancelOrder5,"Заказ удален");
    }

    @Test
    void viewListOrder() throws SQLException, ParseException {
        ArrayList<Order> listAllOrder = new ArrayList<>(1);
        Order order = new Order(1);
        order.setDescription("");
        listAllOrder.add(order);
        Mockito.when(orderRepository.getAll())
                .thenReturn(listAllOrder);
        String viewListOrder1 = orderService.viewListOrder(-1);
        Assertions.assertEquals(viewListOrder1,"у вас нет ни одного заказа");

        String viewListOrder2 = orderService.viewListOrder(1);
        Assertions.assertEquals(viewListOrder2,"-42: \n");

    }
}