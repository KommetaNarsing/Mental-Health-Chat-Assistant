import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new EmptyHandler());
        server.createContext("/api/submit", new SurveySubmitHandler());
        server.createContext("/api/survey", new SurveyHandler());
        server.createContext("/api/chat", new ChatHandler());
        server.createContext("/api/signIn", new SignInHandler());
        server.createContext("/api/isSignedIn", new SessionHandler());
        server.createContext("/api/logout", new LogoutHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8080");
    }
}