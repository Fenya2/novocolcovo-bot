package db;

import entities.User;

public class DBStrub implements DB {

    @Override
    public boolean haveUser(int id) {
        return false;
    }

    @Override
    public User getUserByPlatformAndIdOnPlatform(int idOnPlatform, String platform) {
        return null;
    }

    @Override
    public String getUserState(User user) {
        return null;
    }

    @Override
    public void updateUserById(int id, User user) {

    }

    @Override
    public void deleteUserById(int id) {

    }
}
