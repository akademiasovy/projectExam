package general.net;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import general.Utils;
import general.database.Database;
import general.database.Student;

import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
                        if (exchange.getRequestMethod().equals("POST")) {
                            this.handleMain(exchange);
                        } else {
                            this.handleLogin(exchange);
                        }
                        return;
                    }
                }
            }
        } catch (Exception ex) {ex.printStackTrace();}
        this.handleCookieConsent(exchange);
    }

    public void handleMain(HttpExchange exchange) throws Exception {
        String token = Utils.parseToken(exchange);

        if (!TokenHandler.getInstance().isAuthenticated(token)) {
            exchange.sendResponseHeaders(401, 0);
            exchange.close();
            return;
        }

        Student student = Database.getInstance().getCredentials(TokenHandler.getInstance().getUsername(token)).getStudent();

        String data = new String(Utils.readResource("main.html"));
        data = data.replaceAll("\\$FULLNAME\\$",student.getFirstname()+" "+student.getLastname());
        byte[] bytes = data.getBytes();
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
        exchange.close();
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
