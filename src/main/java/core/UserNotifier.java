package core;

import bots.Bot;
import bots.TGBot;
import bots.VkBot;
import db.LoggedUsersRepository;
import models.Platform;

import java.sql.SQLException;

/**
 * Класс для отправки сообщений другим пользоваетелям.
 */
public class UserNotifier {
    /** @see LoggedUsersRepository */
    private final LoggedUsersRepository loggedUsersRepository;

    /** Список доступных ботов, связянных с платформами. */
    private final VkBot vkBot;
    private final TGBot tgBot;

    /**
     * @param loggedUsersRepository таблица с залогинившимися пользователями
     * @param telegramBot телеграм бот
     */
    public UserNotifier(LoggedUsersRepository loggedUsersRepository,
                        TGBot telegramBot,
                        VkBot vkBot
    ) {
        this.loggedUsersRepository = loggedUsersRepository;
        this.tgBot = telegramBot;
        this.vkBot = vkBot;
    }

    /**
     * Отправляет пользователю картинкун на все платформы, с которых пользователь взаимодействет
     * с программой.
     * @param userId id пользователя
     * @param text строка для отправки.
     * @return true, если сообщение отправилось хотя бы на одну платформу, иначе false.
     */
    public boolean sendTextMessage(long userId, String text) throws SQLException {
        boolean flag = false;
        String idonp;
        for(Platform platform : Platform.values()) {
            idonp = loggedUsersRepository.getUserIdOnPlatformByUserIdAndPlatform(userId, platform);
            if(idonp == null) continue;
            switch (platform) {
                case TELEGRAM -> tgBot.sendTextMessage(idonp, text);
                case VK -> vkBot.sendTextMessage(idonp, text);
            }
            flag = true;
        }
        return flag;
    }

    /**
     * Отправляет пользователю картинку н на все платформы, с которых пользователь взаимодействет
     * с программой.
     * @param userId id пользователя
     * @param path путь к картинке на устройстве.
     */
    public void sendPicture(long userId, String path) {
        // TODO сделать.
    }

    /**
     * Отправляет сообщение на указанную платформу указанному пользователю, если тот авторизован на
     * ней
     * @param platform
     * @param userId
     * @return true, если отправилось, иначе false.
     */
    public boolean sendTextMessageOnPlatformIfPossible(Platform platform,
                                                       long userId,
                                                       String text) throws SQLException {
        String idnp = loggedUsersRepository.getUserIdOnPlatformByUserIdAndPlatform(userId, platform);
        if(idnp == null) return false;
        switch (platform) {
            case VK -> vkBot.sendTextMessage(idnp, text);
            case TELEGRAM -> tgBot.sendTextMessage(idnp, text);
        }
        return true;
    }

    /**
     * Возвращает имя пользователя на указанной платформе. Если имя не получается узнать,
     * возвращает null.
     */
    public String getUserDomainOnPlatform(Platform platform, String userIdOnPlatform) {
        switch (platform) {
            case TELEGRAM -> {
                return tgBot.getDomainByUserIdOnPlatform(userIdOnPlatform);
            }
            case VK -> {
                return vkBot.getDomainByUserIdOnPlatform(userIdOnPlatform);
            }
        }
        return null;
    }
}
