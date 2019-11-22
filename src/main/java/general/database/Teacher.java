package general.database;

import javax.persistence.*;
import java.util.Set;

@Entity
@DiscriminatorValue("teacher")
public class Teacher extends User {

    public Teacher() {

    }

}
