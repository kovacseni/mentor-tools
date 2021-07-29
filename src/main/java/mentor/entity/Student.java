package mentor.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_name", nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "github_profile", nullable = false, length = 255)
    private String gitHub;

    private String comment;

    public Student(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Student(String name, String email, String gitHub) {
        this.name = name;
        this.email = email;
        this.gitHub = gitHub;
    }
}