package mentor.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto {

    private Long id;
    private String name;
    private String email;
    private String gitHub;
    private String comment;

    public StudentDto(String name, String email, String gitHub) {
        this.name = name;
        this.email = email;
        this.gitHub = gitHub;
    }

    public StudentDto(String name, String email, String gitHub, String comment) {
        this.name = name;
        this.email = email;
        this.gitHub = gitHub;
        this.comment = comment;
    }
}
