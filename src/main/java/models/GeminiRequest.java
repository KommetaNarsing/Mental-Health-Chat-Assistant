package models;

import java.util.ArrayList;
import java.util.List;

public class GeminiRequest {

    List<Content> contents = new ArrayList<>();

    public GeminiRequest(){

    }
    public GeminiRequest(List<Content> contents) {
        this.contents = contents;
    }

    public List<Content> getContents() {
        return contents;
    }

    public void addUserTextContent(String text){
        Content content = new Content();
        content.addTextPart(text);
        content.addUserRole();

        contents.add(content);
    }

    public void addModelTextContent(String text){
        Content content = new Content();
        content.addTextPart(text);
        content.addModelRole();

        contents.add(content);
    }

    @Override
    public String toString() {
        return "GeminiRequest{" +
                "contents=" + contents +
                '}';
    }
}
