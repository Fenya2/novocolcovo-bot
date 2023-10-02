import config.SQLiteDBconfig;
import db.DBinterface;
import db.SQLiteDB;
import mock.DB;
import ui.UserInterface;

import java.sql.SQLException;
import java.util.ArrayList;

public class App {
    DBinterface db;
    ArrayList<UserInterface> uis;
    App() throws SQLException, ClassNotFoundException {
        uis = new ArrayList<UserInterface>();
        db = new DB();
    }

    public void addUI(UserInterface ui) {
        uis.add(ui);
    }

    public void start() {

    }
}
