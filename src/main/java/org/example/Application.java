package org.example;

import com.sun.net.httpserver.HttpServer;
import org.example.rest_handlers.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Application {
    public static void main(String[] args) {
        loadProperties();
        initDB();
        initServer();
    }

    private static void initServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(AppConfig.port()), 0);

            for (int i = 0; i < 10 ; i++) {
                Thread thread = new Thread(new Worker());
                thread.start();
            }

            server.createContext("/", new DefaultHandler());

            //server.setExecutor(Executors.newFixedThreadPool(4));

            server.start();
            System.out.println("org.example.Server started on port 8080");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadProperties() {
        try {
            Class.forName(AppConfig.class.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initDB() {
        loadDrivers();
        createDatabase();
        createTables();
    }

    private static void loadDrivers() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createDatabase() {
        try (Connection conn = DBManager.getPortConnection();
             Statement stmt = conn.createStatement()) {
            if (AppConfig.dbProtocol().equals("mysql")) {
                String createDatabase = "CREATE DATABASE IF NOT EXISTS " + AppConfig.dbName();
                stmt.executeUpdate(createDatabase);
            } else {
                // Check if database exists
                String checkDbQuery = String.format("SELECT 1 FROM pg_database WHERE datname = '%s'", AppConfig.dbName());
                ResultSet rs = stmt.executeQuery(checkDbQuery);

                if (!rs.next()) {
                    // Create database if it doesn't exist
                    String createDatabase = "CREATE DATABASE " + AppConfig.dbName();
                    stmt.executeUpdate(createDatabase);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void createTables() {
        String sql = loadSqlSchemaFile();
        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement()) {
            // Split SQL statements based on semicolon
            String[] queries = sql.split(";");
            for (String query : queries) {
                query = query.trim();
                if (!query.isEmpty()) {
                    boolean result = stmt.execute(query);
                    System.out.println("query is " + query + " and the result is " + result);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static String loadSqlSchemaFile() {
        try (InputStream is = Application.class.getResourceAsStream("/table_schemas.sql")) {
            if (is == null) {
                throw new RuntimeException("schema file not found");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}