package models;

import java.util.List;

public class SurveyResponses {
    private String userId;
    private List<Response> responses;

    public SurveyResponses() {

    }

    public String getUserId() {
        return userId;
    }

    public List<Response> getResponses() {
        return responses;
    }

    @Override
    public String toString() {
        return "SurveyResponses{" +
                "userId='" + userId + '\'' +
                ", responses=" + responses +
                '}';
    }
}


