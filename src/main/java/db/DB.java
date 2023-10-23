package db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public interface DB {
    Statement getStatement() throws SQLException;
}
