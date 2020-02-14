package general.net;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import general.Config;
import general.PBKDF2WithHmacSHA256;
import general.Utils;
import general.database.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
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
        } else if (reqURI.equals("/students/new")) {
            try {
                this.handleCreateStudent(exchange, username);
            } catch (Exception ex) { ex.printStackTrace(); }
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

    public void handleCreateStudent(HttpExchange exchange, String username) throws Exception {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().put("Allow", Arrays.asList("POST"));
            exchange.sendResponseHeaders(405,0);
            exchange.close();
            return;
        }

        User user = Database.getInstance().getCredentials(username).getUser();
        if (!(user instanceof Teacher)) {
            exchange.sendResponseHeaders(403,0);
            exchange.close();
            return;
        }

        JSONObject studentObject = ((JSONObject)new JSONParser().parse(new InputStreamReader(exchange.getRequestBody())));

        if (!Utils.checkStringField(studentObject.get("firstname"),1,-1) || !Utils.checkStringField(studentObject.get("lastname"),1,-1) ||
                !Utils.checkStringField(studentObject.get("username"),1,-1) || studentObject.get("email") == null || !Utils.checkStringField(studentObject.get("password"),1,-1)) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        Student student = new Student();
        student.setFirstname(String.valueOf(studentObject.get("firstname")));
        student.setLastname(String.valueOf(studentObject.get("lastname")));

        HashSet<Group> groupSet = new HashSet<Group>();

        JSONArray groupArray = (JSONArray) studentObject.get("groups");
        List<Group> groups = Database.getInstance().getGroups();
        for (Object obj : groupArray) {
            int id = Integer.parseInt(String.valueOf(obj));
            for (Group group : groups) {
                if (group.getId() == id) {
                    groupSet.add(group);
                    group.getStudentSet().add(student);
                    System.out.println(group.getName());
                    break;
                }
            }
        }
        student.setGroupSet(groupSet);

        Credentials credentials = new Credentials();
        credentials.setLogin(String.valueOf(studentObject.get("username")));
        credentials.setEmail(String.valueOf(studentObject.get("email")));
        credentials.setSalt(PBKDF2WithHmacSHA256.createSalt(Integer.parseInt(String.valueOf(Config.getInstance().getPBKDF2Config().get("saltLength")))));
        credentials.setIterations(Integer.parseInt(String.valueOf(Config.getInstance().getPBKDF2Config().get("iterations"))));
        credentials.setPassword(PBKDF2WithHmacSHA256.hexHash(String.valueOf(studentObject.get("password")),credentials.getSalt(),credentials.getIterations(),Integer.parseInt(String.valueOf(Config.getInstance().getPBKDF2Config().get("derivedKeyLength")))));
        credentials.setUser(student);
        student.setCredentials(credentials);

        Database.getInstance().createUser(student);

        exchange.sendResponseHeaders(201, 0);
        exchange.close();
    }

}