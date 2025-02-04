import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new EmptyHandler());
        server.createContext("/submit", new SurveySubmitHandler());
        server.createContext("/survey", new SurveyHandler());
        server.createContext("/chat", new ChatHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8080");
    }
}