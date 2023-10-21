package db;

import org.apache.log4j.Logger;

import java.sql.Connection;

public interface DB {
    Connection getConnection();
}
