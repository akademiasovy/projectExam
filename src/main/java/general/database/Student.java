package general.database;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "student", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String firstname;
    private String lastname;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idcredentials", referencedColumnName = "id")
    private Credentials credentials;

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

    public Student(int id, String firstname, String lastname) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
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
