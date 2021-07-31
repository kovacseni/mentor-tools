package mentor.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mentor.student.Student;
import mentor.trainingclass.TrainingClass;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRegistrationCommand {

    @NotNull
    private RegistrationStatus status;
}
