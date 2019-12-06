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

public class StudentAPI implements HttpHandler {

    public StudentAPI() {

    }

    public void handle(HttpExchange exchange) throws IOException {
        String token = Utils.parseToken(exchange);

        if (!TokenHandler.getInstance().isAuthenticated(token)) {
            exchange.sendResponseHeaders(401,0);
            exchange.close();
            return;
        }

        String username = TokenHandler.getInstance().getUsername(token);
        String reqURI = exchange.getRequestURI().toString();

        if (reqURI.equals("/students")) {
            this.handleStudentList(exchange, username);
            return;
        }

        exchange.sendResponseHeaders(404,0);
        exchange.close();
    }

    public void handleStudentList(HttpExchange exchange, String username) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().put("Allow", Arrays.asList("GET"));
            exchange.sendResponseHeaders(405,0);
            exchange.close();
            return;
        }

        User user = Database.getInstance().getCredentials(username).getUser();
        if (user instanceof Teacher) {
            List<Student> students = Database.getInstance().getStudents();

            JSONObject root = new JSONObject();
            JSONArray studentArray = new JSONArray();
            for (Student student : students) {
                JSONObject studentObject = new JSONObject();
                studentObject.put("id", student.getId());
                studentObject.put("firstname", student.getFirstname());
                studentObject.put("lastname", student.getLastname());
                studentObject.put("login", student.getCredentials().getLogin());
                JSONArray groups = new JSONArray();
                for (Group group : student.getGroupSet()) {
                    groups.add(group.getName());
                }
                studentObject.put("groups",groups);
                studentObject.put("results",student.getResultSet().size());

                studentArray.add(studentObject);
            }

            root.put("students", studentArray);

            byte[] data = root.toJSONString().getBytes();
            exchange.getResponseHeaders().put("Content-Type", Arrays.asList("application/json"));
            exchange.sendResponseHeaders(200, data.length);
            exchange.getResponseBody().write(data);
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
            exchange.close();
        } else {
            exchange.sendResponseHeaders(403,0);
            exchange.close();
        }
    }

}