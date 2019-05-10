package general;

import general.database.Database;
import general.http.Server;

public class Main {

    public static void main(String[] args) {
        Database.getInstance().getCredentials("idk");
        Server server = new Server();
    }

}
