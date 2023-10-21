import config.SQLiteDBconfig;
import db.DB;
import db.SQLiteDB;
import db.UserContextRepository;
import db.UserRepository;
import models.UserContext;

import java.sql.SQLException;

/**
 * Main class
 */
public class Main {
    /**
     * Entry point
     */
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        DB db = new SQLiteDB(new SQLiteDBconfig("src/main/resources/config/dbconfig.json"));
        UserRepository userRepository = new UserRepository(db);
        UserContextRepository ucr = new UserContextRepository(db, userRepository);
        System.out.println(ucr.deleteUserContext(4));
    }
}