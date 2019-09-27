package general.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import general.database.Database;
import general.database.Exam;
import general.database.Groups;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExamAPI implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("POST")) {
            exchange.getResponseHeaders().put("Allow", Arrays.asList("POST"));
            exchange.sendResponseHeaders(405,0);
            exchange.close();
            return;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        String line = br.readLine();
        if (line == null || !line.matches(".+=.+")) {
            exchange.sendResponseHeaders(401,0);
            exchange.close();
            return;
        }

        String token = null;

        String[] clientData = line.split("&");
        boolean authenticated = false;
        for (String s : clientData) {
            String key = s.split("=")[0];
            if (key.equals("token")) {
                token = s.split("=")[1];
                if (TokenHandler.getInstance().isAuthenticated(token)) authenticated = true;
            }
        }
        if (!authenticated) {
            exchange.sendResponseHeaders(401,0);
            exchange.close();
            return;
        }

        String username = TokenHandler.getInstance().getUsername(token);

        String reqURI = exchange.getRequestURI().toString();
        System.out.println(reqURI);
        if (reqURI.equals("/exams")) {
            this.handleExamList(exchange, username);
            return;
        } else if (reqURI.matches("/exams/\\d/")) {
            try {
                this.handleExamInfo(exchange, username, Integer.parseInt(/*Pattern.compile("\\/exams\\/([0-9]+)\\/").matcher(reqURI).group(0)*/reqURI.substring(7,reqURI.length()-1)));
            } catch (Exception ex) {ex.printStackTrace();}
            return;
        }

        exchange.sendResponseHeaders(400,0);
        exchange.close();
    }

    public void handleExamList(HttpExchange exchange, String username) throws IOException {
        List<Exam> exams = Database.getInstance().getExamsByGroups(Database.getInstance().getGroupsByStudent(
                Database.getInstance().getStudentByUsername(username)), true);

        JSONObject root = new JSONObject();

        JSONArray examArray = new JSONArray();
        for (Exam exam : exams) {
            JSONObject examObject = new JSONObject();
            examObject.put("name",exam.getName());
            examObject.put("id",exam.getId());

            examArray.add(examObject);
        }

        root.put("exams",examArray);

        byte[] data = root.toJSONString().getBytes();
        exchange.sendResponseHeaders(200, data.length);
        exchange.getResponseBody().write(data);
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
        exchange.close();
    }

    public void handleExamInfo(HttpExchange exchange, String username, int examID) throws IOException {
        System.out.println("idk123");
        Exam exam = null;

        List<Exam> exams = Database.getInstance().getExamsByGroups(Database.getInstance().getGroupsByStudent(
                Database.getInstance().getStudentByUsername(username)), true);
        for (Exam exam1 : exams) {
            if (exam1.getId() == examID) {
                exam = exam1;
                break;
            }
        }
        if (exam == null) {
            exchange.sendResponseHeaders(403,0);
            exchange.close();
            return;
        }

        JSONObject examObject = new JSONObject();
        examObject.put("id",exam.getId());
        examObject.put("name",exam.getName());
        examObject.put("description",exam.getDescription());
        examObject.put("questions",exam.getQuestions());
        examObject.put("end",exam.getEnd());

        byte[] data = examObject.toJSONString().getBytes();
        System.out.println(data.length);
        exchange.sendResponseHeaders(200, data.length);
        exchange.getResponseBody().write(data);
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
        exchange.close();
    }

}
