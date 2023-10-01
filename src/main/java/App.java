import config.SQLiteDBconfig;
import db.DBinterface;
import db.SQLiteDB;

import java.sql.SQLException;

public class App {
    DBinterface db;
    App() throws SQLException, ClassNotFoundException {
        db = new SQLiteDB(new SQLiteDBconfig("src/main/resources/config/DBConfig.json"));
    }

    public void start() {

    }
}
