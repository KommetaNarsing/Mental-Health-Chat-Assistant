package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
    public static Connection getConnection() {
        String url = String.format("jdbc:%s://%s/%s", AppConfig.dbProtocol(), AppConfig.dbUrl(), AppConfig.dbName());
        String user = AppConfig.dbUser();
        String password = AppConfig.dbPassword();
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getPortConnection() {
        String url = String.format("jdbc:%s://%s/", AppConfig.dbProtocol(), AppConfig.dbUrl());
        String user = AppConfig.dbUser();
        String password = AppConfig.dbPassword();
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
