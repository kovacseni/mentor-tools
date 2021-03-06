package mentor.trainingclass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mentor.syllabus.Syllabus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingClassDto {

    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Syllabus syllabus;

    public TrainingClassDto(String name, LocalDate startDate) {
        this.name = name;
        this.startDate = startDate;
    }

    public TrainingClassDto(String name, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
