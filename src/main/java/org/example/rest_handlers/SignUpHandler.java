package org.example.rest_handlers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.dao.SurveyDao;
import org.example.dao.UserDao;
import org.example.models.User;
import org.example.util.PasswordUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.*;

public class SignUpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            InputStream inputStream = exchange.getRequestBody();
            String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Map<String, String> request = gson.fromJson(requestBody, new TypeToken<Map<String, String>>() {
            }.getType());
            String userName = request.get("username");
            String password = request.get("password");
            User user = UserDao.getUser(userName);
            if(user != null) {
                boolean isUserExists = true;
                byte[] response = ("{\"isUserExists\" : " + isUserExists + "}").getBytes();
                exchange.getResponseHeaders().set("Content-type", "application/json");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, response.length);
                return;
            }

            byte[] salt = new byte[20];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(salt);
            System.out.println(new String(salt));
            String saltString = Base64.getEncoder().encodeToString(salt);

            String hashedPassword  = PasswordUtil.hashPassword(password + saltString);

            // Get user info
            String userId = UUID.randomUUID().toString();
            User insertUser = new User(userId, userName, hashedPassword, saltString);
            UserDao.insertUser(insertUser);
            byte[] response = ("{\"isSuccess\" : " + true + "}").getBytes();
            exchange.getResponseHeaders().set("Content-type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Set-Cookie", "user=" + userId);
            exchange.sendResponseHeaders(200, response.length);
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }
}
