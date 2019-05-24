package general.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import general.database.Database;
import general.database.Student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Login implements HttpHandler {

    public Login() {

    }

    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("POST")) {
            exchange.getResponseHeaders().put("Allow", Arrays.asList("POST"));
            exchange.sendResponseHeaders(405,0);
            exchange.close();
            return;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        String line = br.readLine();
        if (line == null || !line.matches(".+=.+&.+=.+")) {
            exchange.sendResponseHeaders(400,0);
            exchange.close();
            return;
        }

        String[] splitData =  line.split("&");
        String username = splitData[0].split("=")[1];
        String password = splitData[1].split("=")[1];

        if (TokenHandler.getInstance().authenticate(username,password)) {
            Student student = Database.getInstance().getStudentByUsername(username);
            if (student == null) {
                exchange.sendResponseHeaders(401,0);
                exchange.close();
                return;
            }
            byte[] data = ("token="+TokenHandler.getInstance().getToken(username)+"&firstname="+student.getFirstname()+"&lastname="+student.getLastname()).getBytes();
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
}
