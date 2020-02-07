package general.database;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "question", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="idexam", nullable=false)
    private Exam exam;

    @OneToMany(mappedBy="question", fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    private Set<Answer> answers;

    public Question() {

    }

    public Question(int id, String name, Exam exam) {
        this.id = id;
        this.name = name;
        this.exam = exam;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public Set<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }
}
