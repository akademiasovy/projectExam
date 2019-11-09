package general.database;

import javax.persistence.*;

@Entity
@Table(name = "groups", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})

public class Groups {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private String name;

    public Groups() {

    }

    public Groups(int id, String name) {
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
}

