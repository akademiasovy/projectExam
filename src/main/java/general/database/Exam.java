package general.database;

import javax.persistence.*;
import java.sql.Date;
import java.util.Set;

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

    @OneToMany(mappedBy="exam", fetch = FetchType.EAGER)
    private Set<Question> questionSet;

    @ManyToMany(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "grouptoexam",
            joinColumns = { @JoinColumn(name = "idexam") },
            inverseJoinColumns = { @JoinColumn(name = "idgroup") }
    )
    private Set<Group> groupSet;

    @OneToMany(mappedBy="exam", fetch = FetchType.EAGER)
    private Set<Result> resultSet;

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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Exam && this.id == ((Exam) obj).id;
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

    public Set<Question> getQuestionSet() {
        return questionSet;
    }

    public void setQuestionSet(Set<Question> questionSet) {
        this.questionSet = questionSet;
    }

    public Set<Group> getGroupSet() {
        return groupSet;
    }

    public void setGroupSet(Set<Group> groupSet) {
        this.groupSet = groupSet;
    }

    public Set<Result> getResultSet() {
        return resultSet;
    }

    public void setResultSet(Set<Result> resultSet) {
        this.resultSet = resultSet;
    }
}
