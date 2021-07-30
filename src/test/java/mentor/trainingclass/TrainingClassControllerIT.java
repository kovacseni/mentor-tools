package mentor.trainingclass;

import mentor.trainingclass.CreateTrainingClassCommand;
import mentor.trainingclass.TrainingClassDto;
import mentor.trainingclass.UpdateTrainingClassCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(statements = "delete from trainingclasses")
public class TrainingClassControllerIT {

    TrainingClassDto trainingClass;

    @Autowired
    TestRestTemplate template;

    @BeforeEach
    void setUp() {
        template.postForObject("/api/trainingclass",
                new CreateTrainingClassCommand("Újratervezés 2.0", LocalDate.of(2021, 8, 2)),
                TrainingClassDto.class);
        template.postForObject("/api/trainingclass",
                new CreateTrainingClassCommand("Struktúraváltó alap", LocalDate.of(2021, 9, 30)),
                TrainingClassDto.class);
        trainingClass = template.postForObject("/api/trainingclass",
                new CreateTrainingClassCommand("Struktúraváltó haladó", LocalDate.of(2021, 6, 7)),
                TrainingClassDto.class);
    }

    @Test
    void testListTrainingClasses() {

        List<TrainingClassDto> expected = template.exchange("/api/trainingclass",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TrainingClassDto>>() {})
                .getBody();

        assertThat(expected)
                .extracting(TrainingClassDto::getName)
                .containsExactly("Újratervezés 2.0", "Struktúraváltó alap", "Struktúraváltó haladó");
    }

    @Test
    void testFindTrainingClassById() {

        long id = trainingClass.getId();

        TrainingClassDto expected = template.exchange("/api/trainingclass/" + id,
                HttpMethod.GET,
                null,
                TrainingClassDto.class)
                .getBody();

        assertEquals("Struktúraváltó haladó", trainingClass.getName());
        assertEquals(LocalDate.of(2021, 6, 7), trainingClass.getStartDate());
    }

    @Test
    void testUpdateTrainingClass() {

        long id = trainingClass.getId();

        template.put("/api/trainingclass/" + id, new UpdateTrainingClassCommand("Struktúraváltó haladó Java",
                LocalDate.of(2021, 6, 7), LocalDate.of(2021, 8, 9)));

        TrainingClassDto expected = template.exchange("/api/trainingclass/" + id,
                HttpMethod.GET,
                null,
                TrainingClassDto.class)
                .getBody();

        assertEquals("Struktúraváltó haladó Java", expected.getName());
        assertEquals(LocalDate.of(2021, 6, 7), expected.getStartDate());
        assertEquals(LocalDate.of(2021, 8, 9), expected.getEndDate());
    }

    @Test
    void testDeleteTrainingClass() {

        long id = trainingClass.getId();

        template.delete("/api/trainingclass/" + id);


        List<TrainingClassDto> expected = template.exchange("/api/trainingclass",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TrainingClassDto>>() {})
                .getBody();

        assertEquals(2, expected.size());
        assertThat(expected)
                .extracting(TrainingClassDto::getName)
                .containsExactly("Újratervezés 2.0", "Struktúraváltó alap");
    }
}