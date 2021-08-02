package mentor.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mentor.student.Student;
import mentor.trainingclass.TrainingClass;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "registrations")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @AttributeOverride(name = "id", column = @Column(name = "trainingclass_id"))
    private TrainingClass trainingClass;

    @OneToOne
    @AttributeOverride(name = "id", column = @Column(name = "student_id"))
    private Student student;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "registration_status")
    private RegistrationStatus status;

    public Registration(TrainingClass trainingClass, Student student, RegistrationStatus status) {
        this.trainingClass = trainingClass;
        this.student = student;
        this.status = status;
    }
}
