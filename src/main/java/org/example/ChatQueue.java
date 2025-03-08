package org.example;

import com.sun.net.httpserver.HttpExchange;

import java.util.concurrent.LinkedBlockingQueue;

public class ChatQueue {
    private static LinkedBlockingQueue<HttpExchange>  queue = new LinkedBlockingQueue<>();

    public static boolean offer(HttpExchange httpExchange){
       return  queue.offer(httpExchange);
    }

    public static HttpExchange poll()  {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
