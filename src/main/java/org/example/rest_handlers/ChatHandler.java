package org.example.rest_handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.example.dao.ChatDao;
import org.example.dao.SurveyDao;
import org.example.models.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ChatHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            InputStream inputStream = exchange.getRequestBody();
            String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            // String gptResponse = connectToGPT(requestBody);

            List<ChatContent> chatHistory = null;
            String conversationID = createConversation(requestBody);
            System.out.println("Conversation created: " + conversationID);
            if (conversationID != null) {
                chatHistory = getChatHistoryForConversation(conversationID);
            }

            String geminiResponse = connectToGemini(requestBody, chatHistory);
            System.out.println("Received: " + requestBody);

            String response = geminiResponse;

            Gson gson = new Gson();
            Map<String, Object> responseMap = gson.fromJson(response, new TypeToken<Map<String, Object>>() {
            }.getType());
            String botMessage = (String) ((List<Map<String, Map<String, List<Map<String, Object>>>>>) responseMap.get("candidates")).get(0).get("content").get("parts").get(0).get("text");


            Map<String, String> request = gson.fromJson(requestBody, new TypeToken<Map<String, String>>() {
            }.getType());

            createChatContent(request.get("userId"), botMessage, conversationID, "model");
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


    private List<ChatContent> getChatHistoryForConversation(String conversationId) {
        return ChatDao.getChatHistory(conversationId);
    }

    private String createConversation(String requestBody) {
        Gson gson = new Gson();
        Map<String, String> request = gson.fromJson(requestBody, new TypeToken<Map<String, String>>() {
        }.getType());
        ChatContent chatContent = ChatDao.getConversation(request.get("userId"));
        if (chatContent == null) {
            chatContent = createChatContent(request.get("userId"), request.get("userMessage"), null, "user");
            return chatContent.getConversationId();
        } else {
            String timeStamp = chatContent.getTimeStamp();
            if (System.currentTimeMillis() - Long.parseLong(timeStamp) > 300000) {
                chatContent = createChatContent(request.get("userId"), request.get("userMessage"), null, "user");
                return chatContent.getConversationId();
            } else {
                return createChatContent(request.get("userId"), request.get("userMessage"), chatContent.getConversationId(), "user").getConversationId();
            }
        }
    }


    private ChatContent createChatContent(String userId, String message, String conversationId, String role) {
        ChatContent chatContent = new ChatContent();
        chatContent.setContent(message);
        if (conversationId == null) {
            chatContent.setConversationId(UUID.randomUUID().toString());
        } else {
            chatContent.setConversationId(conversationId);
        }
        chatContent.setRole(role);
        chatContent.setTimeStamp(String.valueOf(System.currentTimeMillis()));
        chatContent.setUserId(userId);
        ChatDao.insertChatContent(chatContent);
        return chatContent;
    }

    private String connectToGemini(String requestBody, List<ChatContent> chatHistory) {
        StringBuilder response = new StringBuilder();
        try {
            // Define the URL
            String urlString = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyDWzi9FKvh7Fn-TqWvunS3_vGJGBB1_STQ";
            URL url = new URL(urlString);

            // Create the HTTP connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            Gson gson = new Gson();
            Map<String, String> request = gson.fromJson(requestBody, new TypeToken<Map<String, String>>() {
            }.getType());

            GeminiRequest geminiRequest = getGeminiRequest(request.get("userId"), request.get("userMessage"), chatHistory);

            // Create JSON payload
            String jsonPayload = gson.toJson(geminiRequest);

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
            connection.setRequestProperty("Authorization", "Bearer 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");
            connection.setRequestProperty("Content-Type", "application/json");

            Gson gson = new Gson();
            Map<String, String> request = gson.fromJson(requestBody, new TypeToken<Map<String, String>>() {
            }.getType());

            MistralRequest mistralRequest = getMistralRequest(request.get("userId"), request.get("userMessage"));

            // Create JSON payload
            String jsonPayload = gson.toJson(mistralRequest);

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
        for (Response response : responseList
        ) {
            content = content + "question:" + response.getQuestion() + "\nanswer:" + response.getResponse() + "\n----\n";
        }
        String assistantPrompt = "You are a mental health expert. Your job is to have a conversation with the user based on their MENTAL HEALTH SURVEY QUESTIONS. Your tone should be friendly and empathetic. your job improve the mood of the user and ensure to not hurt user feelings or their mental state.\nMENTAL HEALTH SURVEY QUESTIONS:\n";
        content = assistantPrompt + content;
        Message assistantMessage = new Message(content, Role.system);

        List<Message> messageList = new ArrayList<>();
        messageList.add(assistantMessage);

        messageList.add(new Message(userMessage, Role.user));


        MistralRequest mistralRequest = new MistralRequest("mistralai/Mistral-7B-Instruct-v0.3", 500, false, messageList);

        return mistralRequest;

    }


    private GeminiRequest getGeminiRequest(String userId, String userMessage, List<ChatContent> chatHistory) {

        List<Response> responseList = SurveyDao.getSurveyResponses(userId);
        String surveycontents = "";
        for (Response response : responseList
        ) {
            surveycontents = surveycontents + "question:" + response.getQuestion() + "\nanswer:" + response.getResponse() + "\n----\n";
        }

        String assistantPrompt = "";
        try (InputStream is = this.getClass().getResourceAsStream("Promt.txt") ){
             assistantPrompt = new String(is.readAllBytes());
        }
        catch ( IOException exception){

        }
        assistantPrompt = assistantPrompt.replace("{{survey}}" , surveycontents );
        GeminiRequest geminiRequest = new GeminiRequest();
        geminiRequest.addUserTextContent(assistantPrompt);

        if (chatHistory != null) {
            for (ChatContent chat : chatHistory
            ) {
                if (chat.getRole().equalsIgnoreCase("user")) {
                    geminiRequest.addUserTextContent(chat.getContent());
                } else {
                    geminiRequest.addModelTextContent(chat.getContent());
                }
            }
        }

        System.out.println("Gemini request is " + geminiRequest.toString());
        return geminiRequest;
    }


}

