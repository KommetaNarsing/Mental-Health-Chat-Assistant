package models;

import java.util.ArrayList;
import java.util.List;

public class Content {
  List<Part> parts = new ArrayList<>();
    String role;
    public Content(){

    }

    public Content(List<Part> parts) {
        this.parts = parts;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void  addTextPart(String text) {
      parts.add(new Part(text));
    }

    public void addModelRole(){
        role = "model";
    }

    public void addUserRole(){
     role = "user";
    }
}

 class  Part {
   String text;

   public Part(){

   }

     public String getText() {
         return text;
     }

     public Part(String text) {
         this.text = text;
     }
 }
