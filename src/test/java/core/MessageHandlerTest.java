package core;

import bots.Bot;
import core.service_handlers.handlers.*;
import db.LoggedUsersRepository;
import db.UserContextRepository;
import models.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import strubs.BotStrub;

import java.sql.SQLException;

public class MessageHandlerTest {
    @InjectMocks
    MessageHandler messageHandler;
    @Mock
    private UserContextRepository userContextRepository;
    @Mock
    private LoggedUsersRepository loggedUsersRepository;
    @Mock
    private CommandHandler commandHandler;
    @Mock
    private HandlerEditUserService handlerEditUserService;
    @Mock
    private HandlerCreateOrderService handlerCreateOrderService;
    @Mock
    private HandlerEditOrderService handlerEditOrderService;
    @Mock
    private HandlerCancelOrderService handlerCancelOrderService;
    @Mock
    private HandlerAcceptOrderCourierService handlerAcceptOrderService;
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
    public void testHandleWhenFirstUserMessageSended() throws SQLException {
        Bot bot = new BotStrub();
        Message message = new Message();
        message.setBotFrom(bot);
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
    public void testHandleWhenFirstUserMessageSendedAndItIsStartMessage() throws SQLException {
        Bot bot = new BotStrub();
        Message message = new Message();
        message.setPlatform(Platform.TELEGRAM);
        message.setText("/start");
        message.setBotFrom(bot);
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
    public void testHandleWhenUserHaveContext() throws SQLException {
        Bot bot = new BotStrub();
        Message message = new Message();
        message.setText("some context text. want to create order.");
        message.setUserIdOnPlatform("some id on some platform(telegram).");
        message.setPlatform(Platform.TELEGRAM);
        message.setBotFrom(bot);
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

    /** проверяет работу метода, когда пользователь, написавший сообщение не имеет контекста */
    @Test
    public void testHandleWhenUserHaveNoContext() throws SQLException {
        Bot bot = new BotStrub();
        Message message = new Message();
        message.setText("some context text. want to create order.");
        message.setUserIdOnPlatform("some id on some platform (telegram).");
        message.setPlatform(Platform.TELEGRAM);
        message.setBotFrom(bot);
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
}
