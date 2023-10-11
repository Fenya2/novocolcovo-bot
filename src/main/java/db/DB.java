package db;


import entities.User;

public interface DB {
    /**
     * @return наличие User
     */
    boolean haveUser(int id);

    /**
     * @return возвращает пользователя по id и плотформе если пользователя нет, то возвращает null
     */
    User getUserByPlatformAndIdOnPlatform(int idOnPlatform,String platform);

    /**
     * @return возвращает состояние пользователя, если нет состояния возвращает null
     */
    String getUserState(User user);

    /**
     * @param id старого user
     * @param user  обновляем на нового user
     */
    void updateUserById(int id, User user);

    /**
     * @param id удаляем user
     */
    void deleteUserById(int id);
}
