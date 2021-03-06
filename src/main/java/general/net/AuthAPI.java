package general.net;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import general.database.Database;
import general.database.User;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class AuthAPI implements HttpHandler {

    public AuthAPI() {

    }

    public void handle(HttpExchange exchange) throws IOException {
        String reqURI = exchange.getRequestURI().toString();

        if (reqURI.equals("/auth/login")) {
            this.handleLogin(exchange);
            return;
        }

        exchange.sendResponseHeaders(404,0);
        exchange.close();
    }

    public void handleLogin(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("POST")) {
            exchange.getResponseHeaders().put("Allow", Arrays.asList("POST"));
            exchange.sendResponseHeaders(405,0);
            exchange.close();
            return;
        }

        JSONObject reqJSON = null;
        try {
            reqJSON = ((JSONObject)(new JSONParser().parse(new InputStreamReader(exchange.getRequestBody()))));
        } catch (Exception ex) {ex.printStackTrace();}

        if (reqJSON == null) {
            exchange.sendResponseHeaders(400,0);
            exchange.close();
            return;
        }

        String username = String.valueOf(reqJSON.get("username"));
        String password = String.valueOf(reqJSON.get("password"));

        if (TokenHandler.getInstance().authenticate(username,password)) {
            User user = Database.getInstance().getCredentials(username).getUser();
            if (user == null) {
                exchange.sendResponseHeaders(401,0);
                exchange.close();
                return;
            }

            JSONObject resJSON = new JSONObject();
            resJSON.put("token",TokenHandler.getInstance().getToken(username));
            byte[] data = resJSON.toJSONString().getBytes();
            exchange.sendResponseHeaders(200,data.length);
            exchange.getResponseBody().write(data);
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
            exchange.close();
            return;
        }

        exchange.sendResponseHeaders(401,0);
        exchange.close();
    }

    //TODO: Add handleLogout(HttpExchange exchange)
}
