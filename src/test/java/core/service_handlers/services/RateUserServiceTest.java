package core.service_handlers.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import db.DBException;
import db.UserContextRepository;
import db.UserRateRepository;
import db.UserRepository;

/**
 * Класс, проверяющий работу сервиса, работающего с сохранением и выводом рейтинга пользователей.
 */
public class RateUserServiceTest {
    @InjectMocks
    private RateUserService rateUserService;
    @Mock
    private UserRateRepository userRateRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserContextRepository userContextRepository;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Проверяет обновление рейтинга пользователя.
     */
    @Test
    public void testUpdateUserRate() throws DBException {
        List<Long> rate = new ArrayList<Long>();
        rate.add(4L);
        rate.add(1L);
        Mockito.when(userRateRepository.getRateSumAndNumOfOrders(10L))
            .thenReturn(rate);
        Assert.assertEquals(7, rateUserService.updateUserRate(10, 3));
    }

    /**
     * Проверяет, что возвращает getUserRate по данным, которые хранятся в БД.
     */
    @Test
    public void testGetUserRate() throws DBException{
        List<Long> rate = new ArrayList<Long>();
        rate.add(7L);
        rate.add(4L);
        Mockito.when(userRateRepository.getRateSumAndNumOfOrders(10L))
            .thenReturn(rate);
        Assert.assertTrue(Math.abs(1.75 - rateUserService.getUserRate(10)) < 0.0001);
    }

    /**
     * Проверяет, что сервис возвращает корректное сообщение при завершении
     * работы с пользователем
     */
    @Test
    public void testEndSession() throws DBException, SQLException{
        Assert.assertEquals("Готово!", rateUserService.endSession(10L));
    }

    /**
     * Проверяет, что отправляет сообщение со справкой:
     */
    @Test
    public void testGetHelpMessage() {
        Assert.assertEquals("Введите целое число от 1 до 5, чтобы оценить пользователя",
        rateUserService.getHelpMessage());
    }
}
