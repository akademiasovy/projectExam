package general.exams;

import general.database.Exam;

public class ExamManager {

    private static ExamManager instance = null;

    public static ExamManager getInstance() {
        if (instance == null) instance = new ExamManager();
        return instance;
    }

    private ExamManager() {

    }

}
