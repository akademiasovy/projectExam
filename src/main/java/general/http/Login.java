package general.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Arrays;

public class Login implements HttpHandler {

    public Login() {

    }

    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getProtocol().equals("POST")) {
            exchange.getResponseHeaders().put("Allow", Arrays.asList("POST"));
            exchange.sendResponseHeaders(405,0);
            exchange.close();
            return;
        }
        exchange.sendResponseHeaders(200,0);
        exchange.close();
    }
}
