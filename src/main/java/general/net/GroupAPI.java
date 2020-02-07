package general.net;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import general.Utils;
import general.database.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStreamReader;
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
        } else if (reqURI.equals("/groups/new")) {
            this.handleCreateGroup(exchange, username);
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

    public void handleCreateGroup(HttpExchange exchange, String username) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().put("Allow", Arrays.asList("POST"));
            exchange.sendResponseHeaders(405, 0);
            exchange.close();
            return;
        }

        User user = Database.getInstance().getCredentials(username).getUser();
        if (user instanceof Teacher) {
            try {
                JSONObject groupObject = ((JSONObject) new JSONParser().parse(new InputStreamReader(exchange.getRequestBody())));

                Group group = new Group();
                group.setName(String.valueOf(groupObject.get("name")));
                Database.getInstance().createGroup(group);

                exchange.sendResponseHeaders(201, 0);
                exchange.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                exchange.sendResponseHeaders(500, 0);
                exchange.close();
            }
        } else {
            exchange.sendResponseHeaders(403, 0);
            exchange.close();
        }
    }
}