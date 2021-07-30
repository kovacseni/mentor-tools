package mentor.trainingclass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTrainingClassCommand {

    @NotNull
    @NotBlank
    private String name;

    @ValidDates
    private StartEndDates dates;
}
