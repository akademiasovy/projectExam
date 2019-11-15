package general.database;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "student", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class Student extends User {

    @ManyToMany(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "studenttogroup",
            joinColumns = { @JoinColumn(name = "idstudent") },
            inverseJoinColumns = { @JoinColumn(name = "idgroup") }
    )
    private Set<Group> groupSet;

    @OneToMany(mappedBy="student", fetch = FetchType.EAGER)
    private Set<Result> resultSet;

    public Student() {

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
