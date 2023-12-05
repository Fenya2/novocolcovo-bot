package core;

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
    public void testSendTextMessageWhenLoggedUserExist() throws SQLException {
        long userId = 10;
        Platform platform = Platform.TELEGRAM;
        String idOnPlatform = "user id on telegram platform.";
        Mockito.when(loggedUsersRepository.getUserIdOnPlatformByUserIdAndPlatform(userId, platform))
                .thenReturn(idOnPlatform);
        Assert.assertTrue(userNotifier.sendTextMessage(userId, "some text"));
    }

    /**
     * Проверяет случай, кодг нет информации о платформе, с которой зашел пользователь.
     */
    @Test
    public void testSendTextMessageWhenNoLoggedUser() throws SQLException {
        long userId = 10;
        Platform platform = Platform.TELEGRAM;
        String idOnPlatform = "user id on telegram platform.";
        Mockito.when(loggedUsersRepository.getUserIdOnPlatformByUserIdAndPlatform(userId, platform))
                .thenReturn(null);
        Assert.assertFalse(userNotifier.sendTextMessage(userId, "some text"));
    }

    /**
     * Проверяет случай, кога отправить сообщение на указанную платформу удается.
     */
    @Test
    public void testSendTextMessageOnPlatformIfPossibleWhenPossible() throws SQLException {
        long userId = 10;
        Platform platform = Platform.TELEGRAM;
        String idOnPlatform = "user id on telegram platform.";
        Mockito.when(loggedUsersRepository.getUserIdOnPlatformByUserIdAndPlatform(userId, platform))
                .thenReturn("idOnPlatformWeNeeed");
        Assert.assertTrue(userNotifier.sendTextMessageOnPlatformIfPossible(platform, userId, "some text"));
    }

    /**
     * Проверяет случай, кога отправить сообщение на указанную платформу не удается.
     */
    @Test
    public void testSendTextMessageOnPlatformIfPossibleWhenNotPossible() throws SQLException {
        long userId = 10;
        Platform platform = Platform.TELEGRAM;
        String idOnPlatform = "user id on telegram platform.";
        Mockito.when(loggedUsersRepository.getUserIdOnPlatformByUserIdAndPlatform(userId, platform))
                .thenReturn(null);
        Assert.assertFalse(userNotifier.sendTextMessageOnPlatformIfPossible(platform, userId, "some text"));
    }

    /**
     *  Проверяет случай, когда удается получить имя пользователя на платформах
     */
    @Test
    public void testGetUserDomainOnPlatformWhenItsPossible() {
        String userIdOnPlatformTelegram = "userIdOnTelegramPlatform";
        String userIdOnPlatformVk = "userIdOnVKPlatform";
        Mockito.when(telegramBot.getDomainByUserIdOnPlatform(userIdOnPlatformTelegram))
                .thenReturn("telegram tag");
        Mockito.when(vkBot.getDomainByUserIdOnPlatform(userIdOnPlatformVk))
                .thenReturn("vk domain");
        Assert.assertEquals("telegram tag",
                userNotifier.getUserDomainOnPlatform(Platform.TELEGRAM, userIdOnPlatformTelegram)
        );
        Assert.assertEquals("vk domain",
                userNotifier.getUserDomainOnPlatform(Platform.VK, userIdOnPlatformVk)
        );
    }

    /**
     *  Проверяет случай, когда не удается получить имя пользователя на платформах
     */
    @Test
    public void testGetUserDomainOnPlatformWhenItsNotPossible() {
        String userIdOnPlatformTelegram = "userIdOnTelegramPlatform";
        String userIdOnPlatformVk = "userIdOnVKPlatform";
        Mockito.when(telegramBot.getDomainByUserIdOnPlatform(userIdOnPlatformTelegram))
                .thenReturn(null);
        Assert.assertNull(userNotifier.getUserDomainOnPlatform(Platform.TELEGRAM, userIdOnPlatformTelegram));
    }
}
