import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.UserDao;
import models.ChatContent;
import models.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class SignInHandler implements HttpHandler {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            handleCorsPreflight(exchange);
            return;
        }

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            InputStream inputStream = exchange.getRequestBody();
            String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Map<String, String> request = gson.fromJson(requestBody, new TypeToken<Map<String, String>>() {
            }.getType());
            String idTokenString = request.get("token");

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JSON_FACTORY)
                    .setAudience(Collections.singletonList("1079214379310-c4l3ehkkvp73urfrvd2prblcrh484to0.apps.googleusercontent.com"))
                    .build();

            // Verify token
            GoogleIdToken idToken = null;
            try {
                idToken = verifier.verify(idTokenString);
                if (idToken != null) {
                    GoogleIdToken.Payload payload = idToken.getPayload();

                    // Get user info
                    String userId = payload.getSubject();
                    String email = payload.getEmail();
                    String name = (String) payload.get("name");
                    String pictureUrl = (String) payload.get("picture");
                    User user = new User(userId, name);
                    UserDao.insertUser(user);
                    System.out.printf("user id  email name" + userId + " " + name + " " + email);
                    exchange.getResponseHeaders().set("Set-Cookie", "user=" + userId);
                    exchange.sendResponseHeaders(200, -1);
                }
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }

        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }

    }


    private void handleCorsPreflight(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(204, -1); // No Content
    }
}
