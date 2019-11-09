package general.net.server;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import general.Config;

import java.net.InetSocketAddress;

public class HTTPServer extends Server {

    private HttpServer httpServer;

    public HTTPServer() {
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(Integer.parseInt((String)Config.getInstance().get("port"))), 0);
        } catch (Exception ex) {ex.printStackTrace();}
    }

    @Override
    public void start() {
        this.httpServer.start();
    }

    @Override
    public void createContext(String path, HttpHandler httpHandler) {
        this.httpServer.createContext(path, httpHandler);
    }
}
