package mentor.mentor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMentorCommand {

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String email;
}
