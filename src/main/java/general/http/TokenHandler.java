package general.http;

import general.SHA256;
import general.database.Credentials;
import general.database.Database;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TokenHandler {

    public static final String CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    private static TokenHandler instance;

    public static TokenHandler getInstance() {
        if (instance == null) instance = new TokenHandler();
        return instance;
    }

    private Map<String, String> tokens;

    private TokenHandler() {
        this.tokens = new HashMap<String, String>();
    }

    public String getToken(String username) {
        if (this.tokens.containsKey(username)) {
            return this.tokens.get(username);
        } else {
            return null;
        }
    }

    public boolean isAuthenticated(String token) {
        return this.tokens.containsValue(token);
    }

    public boolean authenticate(String username, String password) {
        if (this.tokens.containsValue(username)) return true;
        Credentials credentials = Database.getInstance().getCredentials(username);

        if (credentials != null) {
            if (SHA256.hash(password).equals(credentials.getPassword())) {
                String token = null;
                while (token == null || this.tokens.containsValue(token)) {
                    token = "";
                    for (int i = 0; i < 32; i++) {
                        token += (char)TokenHandler.CHARSET.charAt(new Random().nextInt(TokenHandler.CHARSET.length()));
                    }
                }
                this.tokens.put(username,token);
                return true;
            }
        }

        return false;
    }

}
