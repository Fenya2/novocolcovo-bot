package core.service_handlers.services;

import java.sql.SQLException;
import java.util.List;

import db.DBException;
import db.UserContextRepository;
import db.UserRateRepository;
import models.UserContext;
import models.UserState;

/**
 * Сервис, реализующий проставление рейтинга одного пользователя другому
 */
public class RateUserService extends Service {

    /** @see UserRateRepository */
    private UserRateRepository userRateRepository;

    public RateUserService(UserContextRepository userContextRepository, UserRateRepository userRateRepository) {
        super(userContextRepository);
        this.userRateRepository = userRateRepository;
    }

    /**
     * обновляет рейтинг пользователя по новой оценке.
     * Если пользователя еще нет в таблице, добавляет.
     * 
     * @param userId  id пользователя
     * @param newRate новый рейтинг, который получил пользователь
     */
    public void updateUserRate(long userId, long newRate) throws DBException {
        if (!userRateRepository.haveUser(userId)) {
            userRateRepository.save(userId);
        }
        List<Long> oldRate = userRateRepository.getRateSumAndNumOfOrders(userId);
        userRateRepository.updateRate(userId, oldRate.get(0) + newRate, oldRate.get(1) + 1);
    }

    public double getUserRate(long userId) throws DBException {
        if (!userRateRepository.haveUser(userId)) {
            userRateRepository.save(userId);
        }
        List<Long> rate = userRateRepository.getRateSumAndNumOfOrders(userId);
        if (rate.get(1) == 0) {
            return 0.0;
        }
        return (double) rate.get(0) / (double) rate.get(1);
    }

    @Override
    public String endSession(long userId) throws SQLException {
        userContextRepository.updateUserContext(userId, new UserContext(UserState.NO_STATE));
        return "Готово!";
    }

    @Override
    public String getHelpMessage() {
        return "Введите целое число от 1 до 5, чтобы оценить пользователя";
    }

}
