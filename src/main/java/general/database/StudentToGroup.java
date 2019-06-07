package general.database;

import javax.persistence.*;

@Entity
@Table(name = "studenttogroup", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class StudentToGroup {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private int idstudent;
    private int idgroup;

    public StudentToGroup() {

    }

    public StudentToGroup(int id, int idstudent, int idgroup) {
        this.id = id;
        this.idstudent = idstudent;
        this.idgroup = idgroup;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdstudent() {
        return idstudent;
    }

    public void setIdstudent(int idstudent) {
        this.idstudent = idstudent;
    }

    public int getIdgroup() {
        return idgroup;
    }

    public void setIdgroup(int idgroup) {
        this.idgroup = idgroup;
    }
}
