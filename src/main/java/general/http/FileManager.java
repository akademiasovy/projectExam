package general.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import general.Utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileManager implements HttpHandler {

    private Map<String, String> fileMap;

    public FileManager() {
        this.fileMap = new HashMap<String, String>();
    }

    public void handle(HttpExchange exchange) throws IOException {
        String reqURI = exchange.getRequestURI().toString();
        if (reqURI.length() < 10) {
            exchange.sendResponseHeaders(404,0);
            exchange.close();
            return;
        }
        String resName = reqURI.substring(10);

        if (this.fileMap.containsKey(resName)) {
            byte[] data = Utils.readResource(this.fileMap.get(resName));
            exchange.sendResponseHeaders(200, data.length);
            exchange.getResponseBody().write(data);
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
            exchange.close();
            return;
        }

        exchange.sendResponseHeaders(404,0);
        exchange.close();
    }

    public void addFile(String webPath, String resPath) {
        this.fileMap.put(webPath,resPath);
    }

    public void removeFile(String webPath) {
        this.fileMap.remove(webPath);
    }

}
