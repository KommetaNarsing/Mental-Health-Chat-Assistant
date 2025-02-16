import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class EmptyHandler  implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try(InputStream inputStream = this.getClass().getResourceAsStream("index.html")){
            byte[] response = inputStream.readAllBytes();
            exchange.getResponseHeaders().set("Content-type", "text/html");

            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, response.length);

            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }
}
