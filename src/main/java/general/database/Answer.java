package general.database;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "answer", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class Answer {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private String name;

    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean correct;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="idquestion", nullable=false)
    private Question question;

    public Answer() {

    }

    public Answer(int id, Question question, String name, boolean correct) {
        this.id = id;
        this.question = question;
        this.name = name;
        this.correct = correct;
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

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
