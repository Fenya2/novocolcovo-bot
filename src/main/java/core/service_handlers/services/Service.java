package core.service_handlers.services;

import db.UserContextRepository;

import java.sql.SQLException;

public abstract class Service {
    /** думается, что любому маломальски проработанному сервису нужна таблица контекста */
    protected UserContextRepository userContextRepository;

    public Service(UserContextRepository userContextRepository) {
        this.userContextRepository = userContextRepository;
    }

    /**
     * Нанинает сессию с пользователем, меняя контекст его взаимодействия с программой.
     * @param userId идентификатор пользователя, с которым нужно начать сессию.
     * @return приветственное текстовое сообщение сервиса.
     */
    public abstract String startSession(long userId);

    /**
     * Завершает сессию с пользователем, удаляя его контекст.
     * @param userId идентификатор пользователя.
     * @return
     */
    public abstract String endSession(long userId) throws SQLException;

    /**
     * Формальность, чтобы у каждого сервиса была справка по работе с ним.
     * @return
     */
    public abstract String getHelpMessage();
}
