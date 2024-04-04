package com.payment.paymentcodechallenge.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Z.Zhang
 */
@Component
public class ConnectDB {

    private final Logger log = LoggerFactory.getLogger(ConnectDB.class);

    /**
     * Connect to the testDB.db database, db file located in the project root
     * @return the Connection object
     */
    public Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:testDB.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            log.error("Connection to DB error, e:", e);
        }
        return conn;
    }
}
