package mentor.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mentor.trainingclass.TrainingClass;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingClassRegistrationDto {

    private TrainingClass trainingClass;
    private Long studentId;
    private String studentName;
    private RegistrationStatus status;
}
