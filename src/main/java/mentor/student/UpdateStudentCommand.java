package mentor.student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentCommand {

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String email;

    @NotNull
    @NotBlank
    private String gitHub;

    private String comment;

    public UpdateStudentCommand(String name, String email, String gitHub) {
        this.name = name;
        this.email = email;
        this.gitHub = gitHub;
    }
}
