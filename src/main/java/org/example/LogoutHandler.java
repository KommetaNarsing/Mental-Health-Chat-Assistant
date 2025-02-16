package org.example;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.dao.UserDao;
import org.example.models.User;

import java.io.IOException;

public class LogoutHandler implements HttpHandler {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            Headers requestHeaders = exchange.getRequestHeaders();
            String cookieString = requestHeaders.getFirst("Cookie");
            String[] allCookie = cookieString.split("; ");
            boolean isValidUser = false;
            for (String cookie: allCookie
            ) {
                if(cookie.startsWith("user=")) {
                    User user = UserDao.getUser(cookie.substring(5));
                    if(user != null) {
                        isValidUser = true;
                    }
                }
            }

            if(isValidUser) {
                exchange.getResponseHeaders().set("Set-Cookie", "user=; Max-Age=0; Path=/");
            }

            exchange.getResponseHeaders().set("Content-type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, -1);

        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }
}
