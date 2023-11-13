package core.service_handlers.services;

import core.UserNotifier;
import db.OrderRepository;
import db.UserContextRepository;
import models.Order;
import models.OrderStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.text.ParseException;

/**Класс тестирующий {@link CloseOrderClientService CloseOrderClientService}*/
public class CloseOrderClientServiceTest {

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }
    @InjectMocks
    private CloseOrderClientService closeOrderClientService;
    @Mock
    private UserContextRepository userContextRepository;
    @Mock
    private OrderRepository orderRepository;

    /**
     * Тестирует случаи: <br>
     * 1)Заказчик ввел /yes
     * 2)Заказчик ввел /no
     * 3)Заказчик ввел любое другое значение
     */
    @Test
    public void continueSession() throws SQLException, ParseException {

        Mockito.when(orderRepository.getOrderByIdUserAndStatus(
                1, OrderStatus.CLOSING)
        ).thenReturn( new Order(1));

        UserNotifier userNotifier = Mockito.mock(UserNotifier.class);
        closeOrderClientService.setMessageSender(userNotifier);

        String continueSession1 = closeOrderClientService.continueSession(
                1,"/yes"
        );
        Assert.assertEquals("Заказ успешно закрыт",continueSession1);

        String continueSession2 = closeOrderClientService.continueSession(
                1,"/no"
        );
        Assert.assertEquals(
                "Заказ не закрыт. Свяжитесь с курьером",
                continueSession2
        );

        String continueSession3 = closeOrderClientService.continueSession(
                1,"any"
        );
        Assert.assertEquals(
                "Извините я вас не понимаю. Напиши /help",
                continueSession3
        );
    }
}