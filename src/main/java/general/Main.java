package general;

import general.database.Database;
import general.net.*;
import general.net.server.*;

public class Main {

    public static void main(String[] args) throws Exception {
        //TODO: Delete
        Database.getInstance().deleteResults();
        //System.out.println(PBKDF2WithHmacSHA256.hexHash("password","salt",4096,32));

        /*Student student = Database.getInstance().getStudentByUsername("rm");
        List<Group> groups = Database.getInstance().getGroupsByStudent(student);
        List<Exam> exams = Database.getInstance().getExamsByGroups(groups, true);
        for (Exam exam : exams) {
            System.out.println(exam.getName());
        }*/

        Config.getInstance();

        Server server = new HTTPServer();
        server.createContext("/",new Index());
        server.createContext("/login",new Login());
        server.createContext("/exams",new ExamAPI());
        server.createContext("/password",new PasswordAPI());

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
        server.createContext("/resources",fileManager);

        server.start();
    }

}
