package models;

public class Response {
    String question;
    String response;

    public Response(String question, String response) {
        this.question = question;
        this.response = response;
    }

    public String getQuestion() {
        return question;
    }

    public String getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "Response{" +
                "question='" + question + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
