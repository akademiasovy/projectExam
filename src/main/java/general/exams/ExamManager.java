package general.exams;

import general.database.Database;
import general.database.Exam;
import general.database.Result;
import general.database.Student;

import java.util.ArrayList;
import java.util.List;

public class ExamManager {

    private static ExamManager instance = null;

    private List<StartedExam> exams;

    public static ExamManager getInstance() {
        if (instance == null) instance = new ExamManager();
        return instance;
    }

    private ExamManager() {
        this.exams = new ArrayList<StartedExam>();
    }

    public boolean startExam(int studentID, int examID) {
        if (this.getExam(studentID) == null) {
            this.exams.add(new StartedExam(studentID,examID));
            return true;
        }
        return false;
    }

    public boolean startExam(Student student, Exam exam) {
        if (this.getExam(student.getId()) == null) {
            this.exams.add(new StartedExam(student,exam));
            return true;
        }
        return false;
    }

    public Result finalizeExam(StartedExam exam) {
        if (exam == null || !this.exams.contains(exam) || !exam.isDone()) return null;
        Result result = new Result();
        result.setStudent(exam.getStudent());
        result.setExam(exam.getExam());
        result.setCorrect(exam.getCorrect());

        if (Database.getInstance().saveResult(result)) this.exams.remove(exam);
        return result;
    }

    public StartedExam getExam(int studentID) {
        for (StartedExam exam : this.exams) {
            if (exam.getStudent().getId() == studentID) return exam;
        }
        return null;
    }

}
