package general.exams;

import general.database.Database;
import general.database.Exam;
import general.database.Question;
import general.database.Student;

import java.util.*;

public class StartedExam {

    private Date startTime;

    private Student student;
    private Exam exam;

    private QAPair[] questions;
    private int currentQuestion;

    public StartedExam(int studentID, int examID) {
        this.startTime = new Date();

        this.student = Database.getInstance().getStudentByID(studentID);
        this.exam = Database.getInstance().getExamByID(examID);

        this.questions = new QAPair[this.exam.getQuestions()];
        List<Question> randomQuestions = new ArrayList<Question>(this.exam.getQuestionSet());
        Collections.shuffle(randomQuestions);
        for (int i = 0; i < this.questions.length; i++) {
            this.questions[i] = new QAPair(randomQuestions.get(i));
        }

        this.currentQuestion = 0;
    }

    public StartedExam(Student student, Exam exam) {
        this.startTime = new Date();

        this.student = student;
        this.exam = exam;

        this.questions = new QAPair[this.exam.getQuestions()];
        List<Question> randomQuestions = new ArrayList<Question>(this.exam.getQuestionSet());
        Collections.shuffle(randomQuestions);
        for (int i = 0; i < this.questions.length; i++) {
            this.questions[i] = new QAPair(randomQuestions.get(i));
        }

        this.currentQuestion = 0;
    }

    public boolean nextQuestion(int answer) {
        if (this.currentQuestion < this.questions.length) {
            this.questions[this.currentQuestion].answer(answer);
            this.currentQuestion++;
            if (this.currentQuestion < this.questions.length) return true;
        }
        return false;
    }

    public boolean isDone() {
        return this.currentQuestion >= this.questions.length;
    }

    public int getCorrect() {
        int correct = 0;
        for (QAPair question : this.questions) {
            if (question.isAnsweredCorrectly()) correct++;
        }
        return correct;
    }

    public int getCurrentQuestionNumber() {
        return this.currentQuestion;
    }

    public QAPair getCurrentQuestion() {
        if (this.currentQuestion < this.questions.length) return this.questions[this.currentQuestion];
        return null;
    }

    public Student getStudent() {
        return student;
    }

    public Exam getExam() {
        return exam;
    }

    public Date getStartTime() {
        return startTime;
    }
}
