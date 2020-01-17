package general;

import general.database.Credentials;
import general.database.Database;
import general.database.Student;
import general.net.*;
import general.net.server.*;

public class Main {

    public static void main(String[] args) {
        /*TODO:
        *
        * General:
        *   Add forgot password functionality
        * Security:
        *   Store tokens in httpOnly secure cookies ------> add './auth/logout' to AuthAPI
        *   Better tokens (stronger, better, length and other settings in config)
        * Teacher:
        *   View started exams (ability to terminate)
        *   Manage and add/remove students (assign groups)
        *   Manage and add/remove groups
        *   Manage and add/remove teachers (can't remove self)
        *   Manage and add/remove exams (add or change questions, assign groups, etc.)
        *   View results (per student/per exam)
        *
        * */

        //TODO: Delete
        //Database.getInstance().deleteResults();

        /*Student student = new Student();
        student.setFirstname("Firstname");
        student.setLastname("Lastname");
        Credentials credentials = new Credentials();
        credentials.setLogin("testuser");
        credentials.setEmail("user@test123.gov");
        credentials.setSalt(PBKDF2WithHmacSHA256.createSalt(16));
        credentials.setIterations(4096);
        credentials.setPassword(PBKDF2WithHmacSHA256.hexHash("letmein",credentials.getSalt(),credentials.getIterations(),32));
        student.setCredentials(credentials);
        credentials.setUser(student);
        Database.getInstance().createUser(student);*/

        /*Credentials creds = Database.getInstance().getCredentials("ro");
        creds.setPassword(PBKDF2WithHmacSHA256.hexHash("letmein",creds.getSalt(),creds.getIterations(),creds.getPassword().length()/2));
        Database.getInstance().changeCredentials(creds);*/

        Config.getInstance();

        Server server = new HTTPServer();
        server.createContext("/",new Index());
        server.createContext("/auth",new AuthAPI());
        server.createContext("/exams",new ExamAPI());
        server.createContext("/password",new PasswordAPI());
        server.createContext("/students",new StudentAPI());
        server.createContext("/groups",new GroupAPI());

        FileManager fileManager = server.getFileManager();
        fileManager.addFile("jquery.js","jquery3.4.1.min.js");
        fileManager.addFile("options-light.png","options-light.png");
        fileManager.addFile("options-dark.png","options-dark.png");
        fileManager.addFile("main-light.css","main-light.css");
        fileManager.addFile("main-dark.css","main-dark.css");
        fileManager.addFile("main.js","main.js");
        fileManager.addFile("progressbar.min.js","progressbar.min.js");
        fileManager.addFile("sortable.min.js","sortable.min.js");
        fileManager.addFile("sortable-light.css","sortable-theme-light.css");
        fileManager.addFile("sortable-dark.css","sortable-theme-dark.css");
        fileManager.addFile("teachermain.css","teachermain.css");
        fileManager.addFile("teachermain.js","teachermain.js");
        fileManager.addFile("select2.min.css","select2.min.css");
        fileManager.addFile("select2.min.js","select2.min.js");
        server.createContext("/resources",fileManager);

        server.start();
    }

}
