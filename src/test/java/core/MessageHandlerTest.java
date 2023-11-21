package core;

import bots.Bot;
import core.service_handlers.handlers.*;
import db.DBException;
import db.LoggedUsersRepository;
import db.LoggingUsersRepository;
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

public class MessageHandlerTest {
    @InjectMocks
    MessageHandler messageHandler;
    @Mock
    private UserContextRepository userContextRepository;
    @Mock
    private LoggedUsersRepository loggedUsersRepository;
    @Mock
    private LoggingUsersRepository loggingUsersRepository;
    @Mock
    private CommandHandler commandHandler;
    @Mock
    private HandlerLoginService handlerLoginService;
    @Mock
    private HandlerEditUserService handlerEditUserService;
    @Mock
    private HandlerCreateOrderService handlerCreateOrderService;
    @Mock
    private HandlerEditOrderService handlerEditOrderService;
    @Mock
    private HandlerCancelOrderService handlerCancelOrderService;
    @Mock
    private HandlerAcceptOrderService handlerAcceptOrderService;
    @Mock
    private HandlerCloseOrderCourierService handlerCloseOrderCourierService;
    @Mock
    private HandlerCloseOrderClientService handlerCloseOrderClientService;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Проверяет логику метода handle, когда пользователь пишет впервые.
     */
    @Test
    public void testHandleWhenFirstUserMessageSended() throws SQLException, DBException {
        Message message = new Message();
        message.setBotFrom(Mockito.mock(Bot.class));
        Mockito.when(
                        loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                                Mockito.any(),
                                Mockito.any()
                        )
                )
                .thenReturn(null); // нет пользователя, который указан в сообщении.
        Assert.assertEquals(1, messageHandler.handle(message));
    }

    /**
     * Проверяет логику метода handle, когда пользователь пишет впервые и это команда /start.
     * (платформа telegram)
     */
    @Test
    public void testHandleWhenFirstUserMessageSentAndItIsStartMessage() throws SQLException, DBException {
        Message message = new Message();
        message.setPlatform(Platform.TELEGRAM);
        message.setText("/start");
        message.setBotFrom(Mockito.mock(Bot.class));
        Mockito.when(
                        loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                                Mockito.any(),
                                Mockito.any()
                        )
                )
                .thenReturn(null); // нет пользователя, который указан в сообщении.
        Assert.assertEquals(1, messageHandler.handle(message));
    }

    /**
     * Проверяет лоику метода, когда пользователь, отправивший сообщение имеет какой-то контекст.
     */
    @Test
    public void testHandleWhenUserHaveContext() throws SQLException, DBException {
        Message message = new Message();
        message.setText("some context text. want to create order.");
        message.setUserIdOnPlatform("some id on some platform(telegram).");
        message.setPlatform(Platform.TELEGRAM);
        message.setBotFrom(Mockito.mock(Bot.class));
        User user = new User();
        user.setId(10);
        UserContext userContext = new UserContext(UserState.ORDER_CREATING);
        Mockito.when(loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                message.getPlatform(),
                message.getUserIdOnPlatform()
        )).thenReturn(user);
        Mockito.when(userContextRepository.getUserContext(user.getId()))
                .thenReturn(userContext);
        Assert.assertEquals(3, messageHandler.handle(message));
    }

    /**
     * Проверяет работу метода, когда пользователь, написавший сообщение не имеет контекста
     */
    @Test
    public void testHandleWhenUserHaveNoContext() throws SQLException, DBException {
        Message message = new Message();
        message.setText("some context text. want to create order.");
        message.setUserIdOnPlatform("some id on some platform (telegram).");
        message.setPlatform(Platform.TELEGRAM);
        message.setBotFrom(Mockito.mock(Bot.class));
        User user = new User();
        user.setId(10);
        UserContext userContext = new UserContext(UserState.NO_STATE);
        Mockito.when(loggedUsersRepository.getUserByPlatformAndIdOnPlatform(
                message.getPlatform(),
                message.getUserIdOnPlatform()
        )).thenReturn(user);
        Mockito.when(userContextRepository.getUserContext(user.getId()))
                .thenReturn(userContext);
        Assert.assertEquals(2, messageHandler.handle(message));
    }

    /**
     * Проверяет случай, кодга пользователь находится в контексте авторизации
     */
    @Test
    public void testHandleWhenUserInAuthContext() throws DBException {
        Message message = new Message();
        message.setText("some context text. want to create order.");
        message.setUserIdOnPlatform("some id on some platform (telegram).");
        message.setPlatform(Platform.TELEGRAM);
        message.setBotFrom(Mockito.mock(Bot.class));
        Mockito.when(loggingUsersRepository.getDomainByFromPlatformAndIdOnPlatform(message.getPlatform(), message.getUserIdOnPlatform()))
                .thenReturn(Mockito.mock(Domain.class)); // возвращает не null
        Assert.assertEquals(4, messageHandler.handle(message));
    }
}
