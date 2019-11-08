package general;

import general.database.Database;
import general.net.ExamAPI;
import general.net.FileManager;
import general.net.Index;
import general.net.Login;
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

        FileManager fileManager = server.getFileManager();
        fileManager.addFile("jquery","jquery3.4.1.min.js");
        fileManager.addFile("options.png","options.png");
        fileManager.addFile("main.css","main.css");
        fileManager.addFile("main.js","main.js");
        fileManager.addFile("progressbar.js","progressbar.js");
        fileManager.addFile("sortable.min.js","sortable.min.js");
        fileManager.addFile("sortable-light.css","sortable-theme-light.css");
        fileManager.addFile("sortable-dark.css","sortable-theme-dark.css");
        server.createContext("/resources",fileManager);

        server.start();
    }

}
