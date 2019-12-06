package general.net;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import general.Utils;
import general.database.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GroupAPI implements HttpHandler {

    public GroupAPI() {

    }

    public void handle(HttpExchange exchange) throws IOException {
        String token = Utils.parseToken(exchange);

        if (!TokenHandler.getInstance().isAuthenticated(token)) {
            exchange.sendResponseHeaders(401, 0);
            exchange.close();
            return;
        }

        String username = TokenHandler.getInstance().getUsername(token);
        String reqURI = exchange.getRequestURI().toString();

        if (reqURI.equals("/groups")) {
            this.handleGroupList(exchange, username);
            return;
        }

        exchange.sendResponseHeaders(404, 0);
        exchange.close();
    }

    public void handleGroupList(HttpExchange exchange, String username) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().put("Allow", Arrays.asList("GET"));
            exchange.sendResponseHeaders(405, 0);
            exchange.close();
            return;
        }

        User user = Database.getInstance().getCredentials(username).getUser();
        if (user instanceof Teacher) {
            List<Group> groups = Database.getInstance().getGroups();

            JSONObject root = new JSONObject();
            JSONArray groupArray = new JSONArray();
            for (Group group : groups) {
                JSONObject groupObject = new JSONObject();
                groupObject.put("id", group.getId());
                groupObject.put("name", group.getName());
                groupObject.put("students", group.getStudentSet().size());
                groupObject.put("exams", group.getExamSet().size());

                groupArray.add(groupObject);
            }

            root.put("groups", groupArray);

            byte[] data = root.toJSONString().getBytes();
            exchange.getResponseHeaders().put("Content-Type", Arrays.asList("application/json"));
            exchange.sendResponseHeaders(200, data.length);
            exchange.getResponseBody().write(data);
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
            exchange.close();
        } else {
            exchange.sendResponseHeaders(403, 0);
            exchange.close();
        }
    }
}
