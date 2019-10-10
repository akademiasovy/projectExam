package general.database;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "groups", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})

public class Group {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private String name;

    @ManyToMany(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "studenttogroup",
            joinColumns = { @JoinColumn(name = "idgroup") },
            inverseJoinColumns = { @JoinColumn(name = "idstudent") }
    )
    private Set<Student> studentSet;

    @ManyToMany(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "grouptoexam",
            joinColumns = { @JoinColumn(name = "idgroup") },
            inverseJoinColumns = { @JoinColumn(name = "idexam") }
    )
    private Set<Exam> examSet;

    public Group() {

    }

    public Group(int id, String name) {
        this.id = id;
        this.name = name;
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

    public Set<Student> getStudentSet() {
        return studentSet;
    }

    public void setStudentSet(Set<Student> studentSet) {
        this.studentSet = studentSet;
    }

    public Set<Exam> getExamSet() {
        return examSet;
    }

    public void setExamSet(Set<Exam> examSet) {
        this.examSet = examSet;
    }
}

