package core.service_handlers.services;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import db.LoggedUsersRepository;
import db.LoggingUsersRepository;
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
    private LoggedUsersRepository loggedUsersRepository;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }
}
