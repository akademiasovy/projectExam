package general.database;

import javax.persistence.*;

@Entity
@Table(name = "question", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class Question {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private String name;
    private int idexam;

    public Question() {

    }

    public Question(int id, String name, int idexam) {
        this.id = id;
        this.name = name;
        this.idexam = idexam;
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

    public int getIdexam() {
        return idexam;
    }

    public void setIdexam(int idexam) {
        this.idexam = idexam;
    }
}
