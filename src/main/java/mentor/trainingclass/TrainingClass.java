package mentor.trainingclass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mentor.syllabus.Syllabus;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trainingclasses")
public class TrainingClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trainingclass_name", nullable = false, length = 255)
    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    @ManyToOne
    private Syllabus syllabus;

    public TrainingClass(String name, LocalDate startDate) {
        this.name = name;
        this.startDate = startDate;
    }
}
