package general.net;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import general.Utils;
import general.database.*;
import general.exams.ExamManager;
import general.exams.QAPair;
import general.exams.StartedExam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class ExamAPI implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        /*if (!exchange.getRequestMethod().equals("POST")) {
            exchange.getResponseHeaders().put("Allow", Arrays.asList("POST"));
            exchange.sendResponseHeaders(405,0);
            exchange.close();
            return;
        }*/

        String token = Utils.parseToken(exchange);

        if (!TokenHandler.getInstance().isAuthenticated(token)) {
            exchange.sendResponseHeaders(401,0);
            exchange.close();
            return;
        }

        String username = TokenHandler.getInstance().getUsername(token);
        String reqURI = exchange.getRequestURI().toString();

        //TODO: Delete SOUT
        System.out.println(reqURI);

        if (reqURI.equals("/exams")) {
            this.handleExamList(exchange, username);
            return;
        } else if (reqURI.matches("/exams/\\d+")) {
            try {
                this.handleExamInfo(exchange, username, Integer.parseInt(reqURI.substring(7,reqURI.length())));
            } catch (Exception ex) {ex.printStackTrace();}
            return;
        } else if (reqURI.matches("/exams/start")) {
            try {
                this.handleStartExam(exchange, username);
            } catch (Exception ex) {ex.printStackTrace();}
            return;
        } else if (reqURI.equals("/exams/assigned")) {
            try {
                this.handleAssignedInfo(exchange, username);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return;
        } else if (reqURI.equals("/exams/assigned/question")) {
                try {
                    this.handleAssignedQuestion(exchange, username);
                } catch (Exception ex) {ex.printStackTrace();}
                return;
        } else if (reqURI.equals("/exams/assigned/answer")) {
            try {
                this.handleAssignedAnswer(exchange, username);
            } catch (Exception ex) {ex.printStackTrace();}
            return;
        } else if (reqURI.equals("/exams/results")) {
            try {
                this.handleResultList(exchange,username);
            } catch (Exception ex) {ex.printStackTrace();}
            return;
        } else if (reqURI.matches("/exams/results/\\d+")) {
            try {
                this.handleResultInfo(exchange, username, Integer.parseInt(reqURI.substring(15,reqURI.length())));
            } catch (Exception ex) {ex.printStackTrace();}
            return;
        }

        exchange.sendResponseHeaders(404,0);
        exchange.close();
    }

    public void handleExamList(HttpExchange exchange, String username) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().put("Allow", Arrays.asList("GET"));
            exchange.sendResponseHeaders(405,0);
            exchange.close();
            return;
        }

        User user = Database.getInstance().getCredentials(username).getUser();
        if (!(user instanceof Student)) {
            exchange.sendResponseHeaders(403,0);
            exchange.close();
            return;
        }
        Student student = (Student)user;

        List<Exam> exams = Database.getInstance().getExams(student, false, false);

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
        exchange.getResponseHeaders().put("Content-Type",Arrays.asList("application/json"));
        exchange.sendResponseHeaders(200, data.length);
        exchange.getResponseBody().write(data);
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
        exchange.close();
    }

    public void handleExamInfo(HttpExchange exchange, String username, int examID) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().put("Allow", Arrays.asList("GET"));
            exchange.sendResponseHeaders(405,0);
            exchange.close();
            return;
        }
        Exam exam = null;

        User user = Database.getInstance().getCredentials(username).getUser();
        if (!(user instanceof Student)) {
            exchange.sendResponseHeaders(403,0);
            exchange.close();
            return;
        }
        Student student = (Student)user;

        List<Exam> exams = Database.getInstance().getExams(student, true, true);
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
        examObject.put("end",exam.getEnd().getTime());

        byte[] data = examObject.toJSONString().getBytes();
        exchange.getResponseHeaders().put("Content-Type",Arrays.asList("application/json"));
        exchange.sendResponseHeaders(200, data.length);
        exchange.getResponseBody().write(data);
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
        exchange.close();
    }

    public void handleStartExam(HttpExchange exchange, String username) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().put("Allow", Arrays.asList("POST"));
            exchange.sendResponseHeaders(405,0);
            exchange.close();
            return;
        }

        User user = Database.getInstance().getCredentials(username).getUser();
        if (!(user instanceof Student)) {
            exchange.sendResponseHeaders(403,0);
            exchange.close();
            return;
        }
        Student student = (Student)user;

        Exam exam = null;

        int examID;
        try {
            String stringExamID = (String)((JSONObject)new JSONParser().parse(new InputStreamReader(exchange.getRequestBody()))).get("id");
            if (stringExamID == null) {
                exchange.sendResponseHeaders(400,0);
                exchange.close();
                return;
            }
            examID = Integer.parseInt(stringExamID);
        } catch (Exception ex) {
            ex.printStackTrace();
            exchange.sendResponseHeaders(400,0);
            exchange.close();
            return;
        }



        List<Exam> exams = Database.getInstance().getExams(student, false, false);
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

        boolean result = ExamManager.getInstance().startExam(student,exam);
        if (!result) {
            exchange.sendResponseHeaders(409, 0);
        } else {
            Date date = new Date();
            if (exam.getStart().getTime() > date.getTime() || exam.getEnd().getTime() < date.getTime()) {
                exchange.sendResponseHeaders(403,0);
                exchange.close();
                return;
            }

            StartedExam startedExam = ExamManager.getInstance().getExam(student.getId());

            JSONObject examObject = new JSONObject();
            examObject.put("id", startedExam.getExam().getId());
            examObject.put("name", startedExam.getExam().getName());
            examObject.put("questions", startedExam.getExam().getQuestions());
            examObject.put("start", startedExam.getStartTime().getTime());
            byte[] data = examObject.toJSONString().getBytes();
            exchange.getResponseHeaders().put("Content-Type",Arrays.asList("application/json"));
            exchange.sendResponseHeaders(201, data.length);
            exchange.getResponseBody().write(data);
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
        }
        exchange.close();
    }

    public void handleAssignedInfo(HttpExchange exchange, String username) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().put("Allow", Arrays.asList("GET"));
            exchange.sendResponseHeaders(405,0);
            exchange.close();
            return;
        }

        User user = Database.getInstance().getCredentials(username).getUser();
        if (!(user instanceof Student)) {
            exchange.sendResponseHeaders(403,0);
            exchange.close();
            return;
        }
        Student student = (Student)user;
        StartedExam exam = ExamManager.getInstance().getExam(student.getId());

        if (exam == null) {
            exchange.sendResponseHeaders(404,0);
            exchange.close();
        } else {
            JSONObject examObject = new JSONObject();
            examObject.put("id",exam.getExam().getId());
            examObject.put("name",exam.getExam().getName());
            examObject.put("questions",exam.getExam().getQuestions());
            examObject.put("start",exam.getStartTime().getTime());
            byte[] data = examObject.toJSONString().getBytes();
            exchange.getResponseHeaders().put("Content-Type",Arrays.asList("application/json"));
            exchange.sendResponseHeaders(200, data.length);
            exchange.getResponseBody().write(data);
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
            exchange.close();
        }
    }

    public void handleAssignedQuestion(HttpExchange exchange, String username) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().put("Allow", Arrays.asList("GET"));
            exchange.sendResponseHeaders(405,0);
            exchange.close();
            return;
        }

        User user = Database.getInstance().getCredentials(username).getUser();
        if (!(user instanceof Student)) {
            exchange.sendResponseHeaders(403,0);
            exchange.close();
            return;
        }
        Student student = (Student)user;
        StartedExam exam = ExamManager.getInstance().getExam(student.getId());

        if (exam == null) {
            exchange.sendResponseHeaders(404,0);
            exchange.close();
        } else {
            QAPair question = exam.getCurrentQuestion();
            if (question == null) {
                byte[] data = ("./exams/results/"+exam.getExam().getId()).getBytes();
                exchange.sendResponseHeaders(410, data.length);
                exchange.getResponseBody().write(data);
                exchange.getResponseBody().flush();
                exchange.getResponseBody().close();
                exchange.close();
            } else {
                JSONObject questionObject = new JSONObject();
                questionObject.put("number", exam.getCurrentQuestionNumber());
                questionObject.put("question", exam.getCurrentQuestion().getQuestion().getName());
                questionObject.put("optionA", exam.getCurrentQuestion().getAnswer(0).getName());
                questionObject.put("optionB", exam.getCurrentQuestion().getAnswer(1).getName());
                questionObject.put("optionC", exam.getCurrentQuestion().getAnswer(2).getName());
                questionObject.put("optionD", exam.getCurrentQuestion().getAnswer(3).getName());
                byte[] data = questionObject.toJSONString().getBytes();
                exchange.getResponseHeaders().put("Content-Type",Arrays.asList("application/json"));
                exchange.sendResponseHeaders(200, data.length);
                exchange.getResponseBody().write(data);
                exchange.getResponseBody().flush();
                exchange.getResponseBody().close();
                exchange.close();
            }
        }
    }

    public void handleAssignedAnswer(HttpExchange exchange, String username) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().put("Allow", Arrays.asList("POST"));
            exchange.sendResponseHeaders(405,0);
            exchange.close();
            return;
        }

        User user = Database.getInstance().getCredentials(username).getUser();
        if (!(user instanceof Student)) {
            exchange.sendResponseHeaders(403,0);
            exchange.close();
            return;
        }
        Student student = (Student)user;
        StartedExam exam = ExamManager.getInstance().getExam(student.getId());

        if (exam == null) {
            exchange.sendResponseHeaders(404,0);
            exchange.close();
        } else {
            String answerStr = null;
            try {
                answerStr = String.valueOf(((JSONObject)(new JSONParser().parse(new InputStreamReader(exchange.getRequestBody())))).get("answer"));
            } catch (Exception ex) {ex.printStackTrace();}

            if (answerStr == null || answerStr.length() != 1 || !Character.isDigit(answerStr.charAt(0))) {
                exchange.sendResponseHeaders(400,0);
                exchange.close();
            } else {
                int answer = Integer.parseInt(answerStr);
                if (answer < 0 || answer > 3) {
                    exchange.sendResponseHeaders(400,0);
                    exchange.close();
                } else {
                    boolean hasQuestion = exam.nextQuestion(answer);
                    if (hasQuestion) {
                        exchange.sendResponseHeaders(200, 0);
                        exchange.close();
                    } else {
                        ExamManager.getInstance().finalizeExam(exam);
                        byte[] data = ("./exams/results/"+exam.getExam().getId()).getBytes();
                        exchange.sendResponseHeaders(201, data.length);
                        exchange.getResponseBody().write(data);
                        exchange.getResponseBody().flush();
                        exchange.getResponseBody().close();
                        exchange.close();
                    }
                }
            }
        }
    }

    public void handleResultList(HttpExchange exchange, String username) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().put("Allow", Arrays.asList("GET"));
            exchange.sendResponseHeaders(405,0);
            exchange.close();
            return;
        }

        User user = Database.getInstance().getCredentials(username).getUser();
        if (!(user instanceof Student)) {
            exchange.sendResponseHeaders(403,0);
            exchange.close();
            return;
        }
        Student student = (Student)user;

        List<Result> results = new ArrayList<Result>(student.getResultSet());
        Collections.sort(results, new Comparator<Result>() {
            @Override
            public int compare(Result o1, Result o2) {
                if (o1.getId() > o2.getId()) {
                    return -1;
                } else if (o1.getId() < o2.getId()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        JSONObject root = new JSONObject();
        JSONArray resultArray = new JSONArray();
        root.put("results",resultArray);

        for (int i = 0; i < results.size(); i++) {
            JSONObject resultObject = new JSONObject();
            resultObject.put("number",results.size()-i);
            resultObject.put("name",results.get(i).getExam().getName());
            resultObject.put("correct",results.get(i).getCorrect());
            resultObject.put("questions",results.get(i).getExam().getQuestions());
            resultArray.add(resultObject);
        }

        byte[] data = root.toJSONString().getBytes();
        exchange.getResponseHeaders().put("Content-Type",Arrays.asList("application/json"));
        exchange.sendResponseHeaders(200, data.length);
        exchange.getResponseBody().write(data);
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
        exchange.close();
    }

    public void handleResultInfo(HttpExchange exchange, String username, int examID) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().put("Allow", Arrays.asList("GET"));
            exchange.sendResponseHeaders(405,0);
            exchange.close();
            return;
        }

        User user = Database.getInstance().getCredentials(username).getUser();
        if (!(user instanceof Student)) {
            exchange.sendResponseHeaders(403,0);
            exchange.close();
            return;
        }
        Student student = (Student)user;

        Set<Result> results = student.getResultSet();

        for (Result result : results) {
            if (result.getExam().getId() == examID) {
                JSONObject resultObject = new JSONObject();
                resultObject.put("id",result.getExam().getId());
                resultObject.put("name",result.getExam().getName());
                resultObject.put("correct",result.getCorrect());
                resultObject.put("questions",result.getExam().getQuestions());

                byte[] data = resultObject.toJSONString().getBytes();
                exchange.getResponseHeaders().put("Content-Type",Arrays.asList("application/json"));
                exchange.sendResponseHeaders(200, data.length);
                exchange.getResponseBody().write(data);
                exchange.getResponseBody().flush();
                exchange.getResponseBody().close();
                exchange.close();
                return;
            }
        }

        exchange.sendResponseHeaders(404,0);
        exchange.close();
    }
}
