package db;

import java.sql.Connection;

public interface DB {
    Connection getConnection();
}
