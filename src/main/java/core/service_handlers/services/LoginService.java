package core.service_handlers.services;

import db.UserContextRepository;

import java.sql.SQLException;

/**
 * Особенный сервис авторизации. Особенный, потому что работает со своей таблицей, ведь
 * изначально о ползователе ничего неизвестно
 */
public class LoginService extends Service {
    public LoginService(UserContextRepository userContextRepository) {
        super(userContextRepository);
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
