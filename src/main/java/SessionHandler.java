import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.UserDao;
import models.User;

import java.io.IOException;
import java.io.OutputStream;

public class SessionHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            handleCorsPreflight(exchange);
            return;
        }

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
              Headers requestHeaders = exchange.getRequestHeaders();
              String cookieString = requestHeaders.getFirst("Cookie");
              String[] allCookie = cookieString.split("; ");
              boolean isSignedIn = false;
              for (String cookie: allCookie
                 ) {
                if(cookie.startsWith("user=")) {
                    User user = UserDao.getUser(cookie.substring(5));
                    if(user != null) {
                        isSignedIn = true;
                    }
                 }
             }

            byte[] response = ("{\"isSignedIn\" : " + isSignedIn + "}").getBytes();

            exchange.getResponseHeaders().set("Content-type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, response.length);

            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();

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
