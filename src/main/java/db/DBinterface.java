package db;

import models.User;

/*
    Интерфейс для взаимодействия с базой данных. Реализует основные взаимодействия с бд в рамках приложения.
 */
public interface DBinterface {
    public void register(User user);
    public void login(User user);
    public void logout(User user);
}
