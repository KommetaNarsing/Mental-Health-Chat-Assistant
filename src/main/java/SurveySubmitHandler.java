import com.sun.net.httpserver.Headers;
import dao.SurveyDao;
import models.SurveyResponses;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


class SurveySubmitHandler implements HttpHandler {
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

            SurveyResponses surveyResponses =  gson.fromJson(requestBody, new TypeToken<SurveyResponses>(){}.getType());
            System.out.println("Received surveyResponses: " + surveyResponses);

            surveyResponses.setUserId(userId);
            SurveyDao.insertSurveyResponse(surveyResponses);

            String response = "{\"sucess\": \"Data received successfully\"}";
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, response.length());

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
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