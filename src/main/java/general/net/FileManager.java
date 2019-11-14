package general.net;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import general.Utils;

import java.io.*;
import java.util.*;

public class FileManager implements HttpHandler {

    private Map<String, String> fileMap;

    public FileManager() {
        this.fileMap = new HashMap<String, String>();
    }

    public void handle(HttpExchange exchange) throws IOException {
        String reqURI = exchange.getRequestURI().toString();
        if (reqURI.length() < 11) {
            exchange.sendResponseHeaders(404,0);
            exchange.close();
            return;
        }
        String resName = reqURI.substring(11);

        if (this.fileMap.containsKey(resName)) {
            byte[] data = Utils.readResource(this.fileMap.get(resName));

            if (resName.endsWith(".css")) {
                exchange.getResponseHeaders().put("Content-Type", Arrays.asList("text/css"));

                //TODO: Use ETags or Last-Modified, maybe delete
                exchange.getResponseHeaders().put("Cache-Control",Arrays.asList("max-age=86400"));
            } else if (resName.endsWith(".js")) {
                exchange.getResponseHeaders().put("Content-Type", Arrays.asList("text/javascript"));
            }

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
