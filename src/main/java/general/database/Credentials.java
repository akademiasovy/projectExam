package general.database;

import javax.persistence.*;

@Entity
@Table(name = "credentials", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class Credentials {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private int idstudent;
    private String login;
    private String password;

    public Credentials() {

    }

    public Credentials(int idstudent, String login, String password) {
        this.idstudent = idstudent;
        this.login = login;
        this.password = password;
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
