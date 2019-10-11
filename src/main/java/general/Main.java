package general;

import general.http.Server;

public class Main {

    public static void main(String[] args) {
        //System.out.println(PBKDF2WithHmacSHA256.hash("password","salt",4096,32));

        /*Student student = Database.getInstance().getStudentByUsername("rm");
        List<Group> groups = Database.getInstance().getGroupsByStudent(student);
        List<Exam> exams = Database.getInstance().getExamsByGroups(groups, true);
        for (Exam exam : exams) {
            System.out.println(exam.getName());
        }*/
        Config.getInstance();
        Server server = new Server();
    }

}
