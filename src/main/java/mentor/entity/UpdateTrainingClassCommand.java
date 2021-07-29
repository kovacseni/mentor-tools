package mentor.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTrainingClassCommand {

    @NotNull
    @NotBlank
    private String name;

    private LocalDate startDate;

    private LocalDate endDate;
}
