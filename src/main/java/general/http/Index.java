package general.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import general.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Index implements HttpHandler {

    public Index() {

    }

    public void handle(HttpExchange exchange) throws IOException {
        try {
            List<String> cookies = exchange.getRequestHeaders().get("Cookie");
            if (cookies != null) {
                for (String cookie : cookies) {
                    if (cookie == null) continue;
                    if (cookie.contains("CookieConsent=Accepted")) {
                        this.handleLogin(exchange);
                        return;
                    }
                }
            }
        } catch (Exception ex) {ex.printStackTrace();}
        this.handleCookieConsent(exchange);
    }

    public void handleLogin(HttpExchange exchange) throws IOException {
        byte[] data = Utils.readResource("login.html");
        exchange.sendResponseHeaders(200, data.length);
        exchange.getResponseBody().write(data);
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
        exchange.close();
    }

    public void handleCookieConsent(HttpExchange exchange) throws IOException {
        byte[] data = Utils.readResource("consent.html");
        exchange.sendResponseHeaders(200, data.length);
        exchange.getResponseBody().write(data);
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
        exchange.close();
    }
}
