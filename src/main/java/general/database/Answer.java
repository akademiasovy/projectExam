package general.database;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "answer", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class Answer {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private int idexam;
    private String name;
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean correct;

    public  Answer() {

    }

    public Answer(int id, int idexam, String name, boolean correct) {
        this.id = id;
        this.idexam = idexam;
        this.name = name;
        this.correct = correct;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdexam() {
        return idexam;
    }

    public void setIdexam(int idexam) {
        this.idexam = idexam;
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
}
