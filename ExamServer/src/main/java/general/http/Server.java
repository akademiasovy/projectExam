package general.http;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class Server {

    private HttpServer httpServer;

    public Server() {
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(81), 0);
            this.httpServer.createContext("/",new Index());
            this.httpServer.createContext("/login",new Login());
            this.httpServer.createContext("/exams",new ExamAPI());

            FileManager fileManager = new FileManager();
            fileManager.addFile("jquery","jquery3.4.1.min.js");
            fileManager.addFile("options.png","options.png");
            fileManager.addFile("main.css","main.css");
            fileManager.addFile("main.js","main.js");
            this.httpServer.createContext("/resource",fileManager);

            this.httpServer.start();
        } catch (Exception ex) {ex.printStackTrace();}
    }

}
