package http;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class Server {

    private HttpServer httpServer;

    public Server() {
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(81), 0);
            this.httpServer.createContext("/",new Index());
            this.httpServer.start();
        } catch (Exception ex) {ex.printStackTrace();}
    }

}
