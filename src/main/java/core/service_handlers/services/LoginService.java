package core.service_handlers.services;

import core.UserNotifier;
import db.*;
import models.Domain;
import models.Platform;
import models.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

/**
 * Особенный сервис авторизации. Особенный, потому что работает со своей таблицей, ведь
 * изначально о ползователе ничего неизвестно.
 */
public class LoginService extends Service {

    /** @see LoggingUsersRepository */
    private LoggingUsersRepository loggingUsersRepository;
    /** @see UserRepository */
    private UserRepository userRepository;
    /** @see LoggedUsersRepository */
    private LoggedUsersRepository loggedUsersRepository;

    /** @see UserNotifier */
    private UserNotifier userNotifier;

    public LoginService(
            UserContextRepository userContextRepository,
            LoggingUsersRepository loggingUsersRepository,
            UserRepository userRepository,
            LoggedUsersRepository loggedUsersRepository
    ) {
        super(userContextRepository);
        this.loggingUsersRepository = loggingUsersRepository;
        this.userRepository = userRepository;
        this.loggedUsersRepository = loggedUsersRepository;
    }

    /**
     * Устанавливает объект, через который будут отправляться сообщения пользователю на другие платформы.
     */
    public void setUserNotifier(UserNotifier userNotifier) {
        this.userNotifier = userNotifier;
    }

    /**
     * Создает запись в таблице logging_users.
     * @param fromPlatform платформа, с которой авторизовывается пользователь
     * @param userIdOnPlatform id пользователя на этой платформе
     */
    public String startSession(Platform fromPlatform, String userIdOnPlatform) throws DBException {
        Domain domain = new Domain()
                .fromPlatform(fromPlatform)
                .idOnPlatform(userIdOnPlatform);
        loggingUsersRepository.saveDomain(domain);
        return "Введите ваш логин пользователя";
    }

    /**
     * Продолжает сессию с авторизующимся пользователем
     * @return следующее сообщение.
     * // TODO раскидать свичи по приватным методам.
     */
    public String continueSession(Platform fromPlatform,
                                  String userIdOnPlatform,
                                  String message,
                                  Domain domain)
            throws DBException, SQLException {
        switch (domain.getLoginContext()) {
            case 0: {
                User user = userRepository.getByLogin(message);
                if(user == null) {
                    return "Такого логина не существует. Попробуйте еще раз.";
                }

                domain.setRequiredLogin(message);
                domain.setLoginContext(1);
                loggingUsersRepository.updateDomain(domain);

                List<Platform> userPlatforms;
                userPlatforms = loggedUsersRepository.getPlatformsByUserId(user.getId());

                StringBuilder response = new StringBuilder(
                        "Введите платформу, куда будет отправлен код подтверждения."
                );
                for(Platform platform : userPlatforms) response.append("\n/%s".formatted(platform));
                return response.toString();
            }
            case 1: {
                Platform verificationPlatform;
                try {
                    verificationPlatform = Platform.valueOf(message.substring(1));
                } catch (IllegalArgumentException e) {
                    return "Платформа указана некорректно. Попробуйте еще раз.";
                    // TODO может вписать платформу, которая не будет указана, но тогда и сообщение не отправится
                }

                int verificationCode = generateVerificationCode();

                domain.setVerificationPlatform(verificationPlatform);
                domain.setLoginContext(2);
                domain.setVerificationCode(verificationCode);
                loggingUsersRepository.updateDomain(domain);

                User user = userRepository.getByLogin(domain.getRequiredLogin());
                userNotifier.sendTextMessageOnPlatformIfPossible(
                        verificationPlatform,
                        user.getId(),
                        "код подтверждения: %d".formatted(verificationCode)
                );

                return "Мы отправили 4-хзначный код на указанную платформу. Введите его, чтобы войти";
            }
            case 2: {
                if(message.length() != 4) {
                    return "Неверный код. Попробуйте еще раз";
                }
                int actualCode = 0;
                try {
                    actualCode = Integer.parseInt(message);
                } catch (NumberFormatException e) {
                    // todo добавить попытки. Либо сделать только одну...
                    return "Неверный код. Попробуйте еще раз";
                }
                if(actualCode != domain.getVerificationCode()) {
                    return "Неверный код. Попробуйте еще раз";
                }

                User user = userRepository.getByLogin(domain.getRequiredLogin());
                loggedUsersRepository.linkUserIdAndUserPlatform(
                        user.getId(),
                        domain.getFromPlatform(),
                        domain.getIdOnPlatform()
                );

                loggingUsersRepository.deleteDomainByFromPlatformAndIdOnPlatform(
                        domain.getFromPlatform(),
                        domain.getIdOnPlatform()
                );

                return "Вы успешно авторизовались.";
            }
        }
        return "Неопределенное поведение";
    }

    /**
     * Отменяет процесс авторизации пользователя.
     */
    public void cancelSession(Platform fromPlatform, String userIdOnPlatform) throws DBException {
        loggingUsersRepository.deleteDomainByFromPlatformAndIdOnPlatform(fromPlatform, userIdOnPlatform);
    }

    /**
     * Генерирует код подтверждения
     */
    private int generateVerificationCode() {
        Random random = new Random();
        return random.nextInt(9000) + 1000;
    }

    @Override
    public String endSession(long userId) throws SQLException {
        return null;
    }

    @Override
    public String getHelpMessage() {
        return null;
    }
}
