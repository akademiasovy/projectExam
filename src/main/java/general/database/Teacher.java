package general.database;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "teacher", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class Teacher extends User {

    public Teacher() {

    }

}
