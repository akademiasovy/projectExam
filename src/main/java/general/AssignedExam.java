package general;

import general.database.Database;
import general.database.Exam;

import java.util.Date;

public class AssignedExam {

    private int idstudent;
    private int idexam;
    private String examName;
    private int examQuestionCount;
    private Date startTime;

    private
    private char[] correctLetters;

    public AssignedExam(int idstudent, int idexam) {
        this.idstudent = idstudent;
        this.idexam = idexam;

        Exam exam = Database.getInstance().getExamById(idexam);
        this.examName = exam.getName();
        this.examQuestionCount = exam.getQuestions();



        this.startTime = new Date();
    }

}
