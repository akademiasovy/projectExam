package general.database;

import general.PBKDF2WithHmacSHA256;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "credentials", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class Credentials {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToOne(mappedBy = "credentials", fetch = FetchType.EAGER)
    private User user;

    private String email;
    private String login;

    private String password;
    private String salt;
    private int iterations;

    public Credentials() {

    }

    public Credentials(String email, String login, String password, String salt, int iterations) {
        this.email = email;
        this.login = login;
        this.password = password;
        this.salt = salt;
        this.iterations = iterations;
    }

    public boolean checkPassword(String password) {
        //return this.password.equals(SHA256.hexHash(password));
        return this.password.equals(PBKDF2WithHmacSHA256.hexHash(password, this.salt, this.iterations,this.password.length()/2));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }
}
