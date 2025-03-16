package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.rest_handlers.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Worker implements Runnable{
    static Map<String, HttpHandler> handlerMap;
    static {
        handlerMap = new HashMap<>();
        handlerMap.put("/api/submit", new SurveySubmitHandler());
        handlerMap.put("/api/survey", new SurveyHandler());
        handlerMap.put("/api/chat", new ChatHandler());
        handlerMap.put("/api/signIn", new SignInHandler());
        handlerMap.put("/api/isSignedIn", new SessionHandler());
        handlerMap.put("/api/logout", new LogoutHandler());
        handlerMap.put("/api/signUp", new SignUpHandler());
    }

    @Override
    public void run() {
        while (true){
            HttpExchange httpExchange = ChatQueue.poll();
            boolean isPathFound = false;
            for (Map.Entry<String, HttpHandler> entry : handlerMap.entrySet()) {
                if(httpExchange.getRequestURI().getPath().contains(entry.getKey())) {
                    try {
                        System.out.println("Request path is " + httpExchange.getRequestURI().getPath());
                        System.out.println("path is " + entry.getKey() + " thread id" + Thread.currentThread().getName()+ " " + Thread.currentThread().getId());
                        isPathFound = true;
                        entry.getValue().handle(httpExchange);
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(!isPathFound) {
                try {
                    new IndexHandler().handle(httpExchange);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
