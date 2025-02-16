package org.example;

import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final int port;
    private static final String dbUser;
    private static final String dbPassword;
    private static final String dbUrl;
    private static final String dbProtocol;
    private static final String dbName;

    static {
        Properties properties = loadProperties();
        port = Integer.parseInt(properties.getProperty("port"));
        dbUser = properties.getProperty("db.user");
        dbPassword = properties.getProperty("db.password");
        dbUrl = properties.getProperty("db.url");
        dbProtocol = properties.getProperty("db.protocol");
        dbName = properties.getProperty("db.name");
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream("app.properties")) {
            if(in == null) {
                throw new RuntimeException("app.properties not found");
            }
            properties.load(in);

            if(!properties.containsKey("port")) {
                properties.setProperty("port", String.valueOf(8080));
            }

            return properties;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private AppConfig() {
    }

    public static int port() {
        return AppConfig.port;
    }

    public static String dbUser() {
        return AppConfig.dbUser;
    }

    public static String dbPassword() {
        return AppConfig.dbPassword;
    }

    public static String dbUrl() {
        return AppConfig.dbUrl;
    }

    public static String dbProtocol() {
        return AppConfig.dbProtocol;
    }

    public static String dbName() {
        return AppConfig.dbName;
    }

}
