package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

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
            for (String cookie : cookies) {
                if (cookie.contains("CookieConsent=Accepted")) {
                    this.handleLogin(exchange);
                    return;
                }
            }
        } catch (Exception ex) {ex.printStackTrace();}
        this.handleCookieConsent(exchange);
    }

    public void handleLogin(HttpExchange exchange) throws IOException {
        byte[] bytes = "Login".getBytes();
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
        exchange.close();
    }

    public void handleCookieConsent(HttpExchange exchange) throws IOException {
        InputStream is = Index.class.getClassLoader().getResourceAsStream("consent.html");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        for (;;) {
            int nread = is.read(buffer);
            if (nread <= 0) {
                break;
            }
            baos.write(buffer, 0, nread);
        }
        byte[] data = baos.toByteArray();
        exchange.sendResponseHeaders(200, data.length);
        exchange.getResponseBody().write(data);
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
        exchange.close();
    }
}
