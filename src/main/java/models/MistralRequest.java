package models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MistralRequest {
    String model;
    @SerializedName("max_tokens")
    int maxToken;
    boolean stream;
    List<Message> messages;

    public MistralRequest() {

    }

    public MistralRequest(String model, int maxToken, boolean stream, List<Message> messages) {
        this.model = model;
        this.maxToken = maxToken;
        this.stream = stream;
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }

    public int getMaxToken() {
        return maxToken;
    }

    public boolean isStream() {
        return stream;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
