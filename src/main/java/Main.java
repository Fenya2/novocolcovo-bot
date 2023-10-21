import config.SQLiteDBconfig;
import db.*;
import models.Order;

import java.sql.SQLException;
import java.text.ParseException;

/**
 * Main class
 */
public class Main {
    /**
     * Entry point
     */
    public static void main(String[] args) throws SQLException, ClassNotFoundException, ParseException {
        DB db = new SQLiteDB(new SQLiteDBconfig("src/main/resources/config/dbconfig.json"));
        UserRepository userRepository = new UserRepository(db);
        OrderRepository or = new OrderRepository(db, userRepository);
        for(Order order : or.getAll()) {
            System.out.println(order);
        }
    }
}