package mentor.mentor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mentors")
public class Mentor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mentor_name", nullable = false, length = 255)
    private String name;

    @Column(name = "mentor_email", nullable = false, length = 255)
    private String email;

    @Enumerated(value = EnumType.STRING)
    private Position position;

    public Mentor(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
