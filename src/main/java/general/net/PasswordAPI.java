package general.net;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import general.Config;
import general.PBKDF2WithHmacSHA256;
import general.Utils;
import general.database.Credentials;
import general.database.Database;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.regex.Pattern;

public class PasswordAPI implements HttpHandler {

    private final Pattern lowerCase, upperCase, digit;

    public PasswordAPI() {
        this.lowerCase = Pattern.compile("[a-z]");
        this.upperCase = Pattern.compile("[A-Z]");
        this.digit = Pattern.compile("[0-9]");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("POST")) {
            exchange.getResponseHeaders().put("Allow", Arrays.asList("POST"));
            exchange.sendResponseHeaders(405,0);
            exchange.close();
            return;
        }

        String token = Utils.parseToken(exchange);
        if (!TokenHandler.getInstance().isAuthenticated(token)) {
            exchange.sendResponseHeaders(401,0);
            exchange.close();
            return;
        }

        String reqURI = exchange.getRequestURI().toString();
        if (reqURI.matches("/password/change")) {
            this.handleChange(exchange);
            return;
        }

        exchange.sendResponseHeaders(400,0);
        exchange.close();
    }

    public void handleChange(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().put("Allow", Arrays.asList("POST"));
            exchange.sendResponseHeaders(405,0);
            exchange.close();
            return;
        }

        String username = TokenHandler.getInstance().getUsername(Utils.parseToken(exchange));

        try {
            JSONObject object = (JSONObject) new JSONParser().parse(new InputStreamReader(exchange.getRequestBody()));
            String oldPassword = String.valueOf(object.get("oldPassword"));
            String newPassword = String.valueOf(object.get("newPassword"));

            Credentials credentials = Database.getInstance().getCredentials(username);
            if (credentials.checkPassword(oldPassword)) {
                if (newPassword.length() >= 8 && this.lowerCase.matcher(newPassword).find() && this.upperCase.matcher(newPassword).find() && this.digit.matcher(newPassword).find()) {
                    credentials.setSalt(PBKDF2WithHmacSHA256.createSalt(Integer.parseInt(String.valueOf(Config.getInstance().getPBKDF2Config().get("saltLength")))));
                    credentials.setPassword(PBKDF2WithHmacSHA256.hexHash(newPassword,credentials.getSalt(),Integer.parseInt(String.valueOf(Config.getInstance().getPBKDF2Config().get("iterations"))),Integer.parseInt(String.valueOf(Config.getInstance().getPBKDF2Config().get("derivedKeyLength")))));
                    Database.getInstance().changeCredentials(credentials);

                    TokenHandler.getInstance().invalidate(username);
                    byte[] data = new byte[0];
                    if (TokenHandler.getInstance().authenticate(username,newPassword)) {
                        JSONObject json = new JSONObject();
                        json.put("token",TokenHandler.getInstance().getToken(username));
                        data = json.toJSONString().getBytes();
                    }

                    exchange.sendResponseHeaders(200,data.length);
                    if (data.length > 0) {
                        exchange.getResponseBody().write(data);
                        exchange.getResponseBody().flush();
                        exchange.getResponseBody().close();
                    }
                    exchange.close();
                    return;
                } else {
                    exchange.sendResponseHeaders(400,0);
                    exchange.close();
                    return;
                }
            } else {
                exchange.sendResponseHeaders(401,0);
                exchange.close();
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        exchange.sendResponseHeaders(500,0);
        exchange.close();
    }

    //TODO: Add handleReset(exchange)
}
