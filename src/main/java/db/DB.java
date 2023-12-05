package db;

import java.sql.SQLException;
import java.sql.Statement;

public interface DB {
    Statement getStatement() throws SQLException;
    public void connect() throws SQLException;
    public void disconnect() throws SQLException;
    public void clearScheme() throws SQLException;
    public void initScheme() throws SQLException;
}
