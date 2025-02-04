import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import dao.SurveyDao;
import models.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


class ChatHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            handleCorsPreflight(exchange);
            return;
        }

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            InputStream inputStream = exchange.getRequestBody();
            String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String gptResponse = connectToGPT(requestBody);
            System.out.println("Received: " + requestBody);

            String response = gptResponse;
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);

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

    private String connectToGPT(String requestBody) {
        StringBuilder response = new StringBuilder();
        try {
            // Define the URL
            String urlString = "https://huggingface.co/api/inference-proxy/together/v1/chat/completions";
            URL url = new URL(urlString);

            // Create the HTTP connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Bearer xyz");
            connection.setRequestProperty("Content-Type", "application/json");

            Gson gson = new Gson();
            Map<String, String> request =  gson.fromJson(requestBody, new TypeToken<Map<String, String>>(){}.getType());

            MistralRequest mistralRequest =  getMistralRequest(request.get("userId"), request.get("userMessage"));

            // Create JSON payload
            String jsonPayload =  gson.toJson(mistralRequest);

            // Write JSON data to the output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read the response
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                // Print the response
                System.out.println("Response: " + response.toString());
            }

            // Close the connection
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    private MistralRequest getMistralRequest(String userId, String userMessage) {

        List<Response> responseList = SurveyDao.getSurveyResponses(userId);
        String content = "";
        for (Response response: responseList
             ) {
            content = content + "question:" + response.getQuestion()+"\nanswer:" + response.getResponse() + "\n----\n";
        }
        String assistantPrompt = "You are a mental health expert. Your job is to have a conversation with the user based on their MENTAL HEALTH SURVEY QUESTIONS. Your tone should be friendly and empathetic. your job improve the mood of the user and ensure to not hurt user feelings or their mental state.\nMENTAL HEALTH SURVEY QUESTIONS:\n";
        content = assistantPrompt + content;
        Message assistantMessage = new Message(content, Role.SYSTEM);

        List<Message> messageList = new ArrayList<>();
        messageList.add(assistantMessage);

        messageList.add(new Message(userMessage, Role.USER));


        MistralRequest mistralRequest = new MistralRequest("mistralai/Mistral-7B-Instruct-v0.3", 1000, false,messageList );

        return mistralRequest;

    }


}

