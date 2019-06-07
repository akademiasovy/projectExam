package general.database;

import javax.persistence.*;

@Entity
@Table(name = "grouptoexam", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class GroupToExam {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private int idgroup;
    private int idexam;

    public GroupToExam() {

    }

    public GroupToExam(int id, int idgroup, int idexam) {
        this.id = id;
        this.idgroup = idgroup;
        this.idexam = idexam;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdgroup() {
        return idgroup;
    }

    public void setIdgroup(int idgroup) {
        this.idgroup = idgroup;
    }

    public int getIdexam() {
        return idexam;
    }

    public void setIdexam(int idexam) {
        this.idexam = idexam;
    }
}
