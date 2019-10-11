package general.database;

import javax.persistence.*;

@Entity
@Table(name = "result", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class Result {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="idstudent", nullable=false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="idexam", nullable=false)
    private Exam exam;

    private int correct;
    private int questions;

    public Result() {

    }

    public Result(int id, Student student, Exam exam, int correct) {
        this.id = id;
        this.student = student;
        this.exam = exam;
        this.correct = correct;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public int getQuestions() {
        return questions;
    }

    public void setQuestions(int questions) {
        this.questions = questions;
    }
}
