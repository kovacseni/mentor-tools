package mentor.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mentor.student.Student;
import mentor.trainingclass.TrainingClass;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDto {

    private TrainingClass trainingClass;

    private Student student;

    private RegistrationStatus status;
}
