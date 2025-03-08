package org.example.rest_handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;


public class IndexHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("thread name" + Thread.currentThread().getName() + " id is " + Thread.currentThread().getId());

        File file = new File("src/main/frontend/dist/index.html");
        if(!file.exists()) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }
        try(InputStream inputStream = new FileInputStream(file)){
            byte[] response = inputStream.readAllBytes();
            exchange.getResponseHeaders().set("Content-type", "text/html");
            exchange.sendResponseHeaders(200, response.length);

            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }
}
