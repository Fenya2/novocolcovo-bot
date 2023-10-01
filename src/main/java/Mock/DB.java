package Mock;

import db.DBinterface;
import models.User;
/*
    Объект-заглушка, пока не написана реализация интерфейса. Ипользовать при разработке бота. Реализуешь интерфейс по своему усмотрению.
 */
public class DB implements DBinterface {

    @Override
    public void register(User user) {

    }

    @Override
    public void login(User user) {

    }

    @Override
    public void logout(User user) {

    }
}
