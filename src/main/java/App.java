import db.DBinterface;
import db.stubDB;
import ui.UserInterface;

import java.sql.SQLException;
import java.util.ArrayList;

public class App {
    DBinterface db;
    ArrayList<UserInterface> uis;
    App() throws SQLException, ClassNotFoundException {
        uis = new ArrayList<UserInterface>();
        db = new stubDB();
    }

    public void addUI(UserInterface ui) {
        uis.add(ui);
    }

    public void start() {

    }
}
