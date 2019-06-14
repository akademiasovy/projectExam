package general.database;

import javax.persistence.*;

@Entity
@Table(name = "assignedquestion", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})

public class AssignedQuestion {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private int idexam;
    private int idstudent;
    private int idquestion;
    private String correctLetter;

    public AssignedQuestion() {

    }

    public AssignedQuestion(int id, int idexam, int idstudent, int idquestion, String correctLetter) {
        this.id = id;
        this.idexam = idexam;
        this.idstudent = idstudent;
        this.idquestion = idquestion;
        this.correctLetter = correctLetter;
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

    public int getIdstudent() {
        return idstudent;
    }

    public void setIdstudent(int idstudent) {
        this.idstudent = idstudent;
    }

    public int getIdquestion() {
        return idquestion;
    }

    public void setIdquestion(int idquestion) {
        this.idquestion = idquestion;
    }

    public String getCorrectLetter() {
        return correctLetter;
    }

    public void setCorrectLetter(String correctLetter) {
        this.correctLetter = correctLetter;
    }
}
