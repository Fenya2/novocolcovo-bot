package core;

import bots.Bot;
import bots.TGBot;
import bots.VkBot;
import db.LoggedUsersRepository;
import models.Platform;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;

public class UserNotifierTest {
    @InjectMocks
    private UserNotifier userNotifier;

    @Mock
    private LoggedUsersRepository loggedUsersRepository;
    @Mock
    private TGBot telegramBot;

    @Mock
    private VkBot vkBot;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Проверяет случай, когда есть информация о платформе, с которой зашел пользователь.
     */
    @Test
    public void testSendTextMessageWhenNoLoggedUser() throws SQLException {
        long userId = 10;
        Platform platform = Platform.TELEGRAM;
        String idOnPlatform = "user id on telegram platform.";
        Mockito.when(loggedUsersRepository.getUserIdOnPlatformByUserIdAndPlatform(userId, platform))
                .thenReturn(idOnPlatform);
        Assert.assertTrue(userNotifier.sendTextMessage(userId, "some text"));
    }
}
