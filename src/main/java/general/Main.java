package general;

import general.database.Database;
import general.database.Exam;
import general.database.Groups;
import general.database.Student;
import general.http.Server;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        /*Student student = Database.getInstance().getStudentByUsername("rm");
        List<Groups> groups = Database.getInstance().getGroupsByStudent(student);
        List<Exam> exams = Database.getInstance().getExamsByGroups(groups, true);
        for (Exam exam : exams) {
            System.out.println(exam.getName());
        }*/

        Server server = new Server();
    }

}
