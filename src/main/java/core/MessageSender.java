package core;

import bots.Bot;
import db.LoggedUsersRepository;
import models.Platform;

import java.sql.SQLException;
import java.util.BitSet;

/**
 * Класс для отправки сообщений другим пользоваетелям.
 */
public class MessageSender {
    /** @see LoggedUsersRepository */
    private final LoggedUsersRepository loggedUsersRepository;

    /** Список доступных ботов, связянных с платформами. */
    private final Bot telegramBot;
    private final Bot vkBot;

    /**
     * @param loggedUsersRepository таблица с залогинившимися пользователями
     * @param telegramBot телеграм бот
     */
    public MessageSender(LoggedUsersRepository loggedUsersRepository,
                         Bot telegramBot,
                         Bot vkBot
    ) {
        this.loggedUsersRepository = loggedUsersRepository;
        this.telegramBot = telegramBot;
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
                case TELEGRAM -> telegramBot.sendTextMessage(idonp, text);
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
        // todo сделать.
    }
}
