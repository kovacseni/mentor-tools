package mentor.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStudentCommand {

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String email;

    @NotNull
    @NotBlank
    private String gitHub;
}
