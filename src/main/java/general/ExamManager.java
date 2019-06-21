package general;

import general.database.AssignedQuestion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamManager {

    private static ExamManager instance = null;

    public static ExamManager getInstance() {
        if (instance == null) instance = new ExamManager();
        return instance;
    }


    private Map<Integer, List<Integer>> questionMap;

    private ExamManager() {
        this.questionMap = new HashMap<Integer, List<Integer>>();
    }

    public void startExam() {

    }

}
