package org.example;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.dao.SurveyDao;

import java.io.IOException;
import java.io.OutputStream;

public class SurveyHandler  implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String userId = "";

        Headers requestHeaders = exchange.getRequestHeaders();
        String cookieString = requestHeaders.getFirst("Cookie");
        String[] allCookie = cookieString.split("; ");
        for (String cookie: allCookie
        ) {
            if(cookie.startsWith("user=")) {
                userId = cookie.substring(5);
            }
        }

        boolean isSurveyTaken = SurveyDao.IsSurveyTaken(userId);
        byte[] response = ("{\"isSurveyTaken\" : " + isSurveyTaken + "}").getBytes();

        exchange.getResponseHeaders().set("Content-type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, response.length);

        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();

    }
}
