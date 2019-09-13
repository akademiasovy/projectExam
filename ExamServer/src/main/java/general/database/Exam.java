package general.database;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "exam", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class Exam {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private String name;
    private String description;
    private int questions;
    private Date start;
    private Date end;

    public Exam() {

    }

    public Exam(int id, String name, String description, int questions, Date start, Date end) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.questions = questions;
        this.start = start;
        this.end = end;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuestions() {
        return questions;
    }

    public void setQuestions(int questions) {
        this.questions = questions;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
