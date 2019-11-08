package general.net.server;

import com.sun.net.httpserver.HttpHandler;
import general.net.FileManager;

public abstract class Server {

    private FileManager fileManager;

    public Server() {
        this.fileManager = new FileManager();
    }

    public abstract void start();
    public abstract void createContext(String path, HttpHandler httpHandler);

    public FileManager getFileManager() {
        return this.fileManager;
    }

}
