package mentor.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mentor.student.Student;
import mentor.trainingclass.TrainingClass;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRegistrationDto {

    private Student student;
    private Long trainingClassId;
    private String trainingClassName;
    private RegistrationStatus status;
}
