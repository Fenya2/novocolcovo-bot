package new_core.handlers;

import db.LoggedUsersRepository;
import db.UserContextRepository;
import new_core.handlers.service_handlers.EditUserServiceHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;

public class MessageHandlerTest {
    @InjectMocks
    private MessageHandler messageHandler;

    @Mock
    private UserContextRepository userContextRepository;
    @Mock
    private LoggedUsersRepository loggedUsersRepository;
    @Mock
    private CommandHandler commandHandler;
    @Mock
    private EditUserServiceHandler editUserServiceHandler;

    /** Считаем, что база данных рабоатает идеально */
    @Before
    public void init() throws SQLException {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHandleWhenNoLoggedUser() {

    }
}
