package mentor.trainingclass;

import mentor.syllabus.CreateSyllabusCommand;
import mentor.syllabus.SyllabusDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.jdbc.Sql;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(statements = "delete from trainingclasses")
@Sql(statements = "delete from syllabuses")
public class TrainingClassControllerIT {

    TrainingClassDto trainingClass;

    @Autowired
    TestRestTemplate template;

    @BeforeEach
    void setUp() {
        template.postForObject("/api/trainingclasses",
                new CreateTrainingClassCommand("Újratervezés 2.0", LocalDate.of(2021, 8, 2)),
                TrainingClassDto.class);
        template.postForObject("/api/trainingclasses",
                new CreateTrainingClassCommand("Struktúraváltó alap", LocalDate.of(2021, 9, 30)),
                TrainingClassDto.class);
        trainingClass = template.postForObject("/api/trainingclasses",
                new CreateTrainingClassCommand("Struktúraváltó haladó", LocalDate.of(2021, 6, 7)),
                TrainingClassDto.class);
    }

    @Test
    void testListTrainingClasses() {

        List<TrainingClassDto> expected = template.exchange("/api/trainingclasses",
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

        TrainingClassDto expected = template.exchange("/api/trainingclasses/" + id,
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

        template.put("/api/trainingclasses/" + id, new UpdateTrainingClassCommand("Struktúraváltó haladó Java",
                new StartEndDates(LocalDate.of(2021, 6, 7), LocalDate.of(2021, 8, 9))));

        TrainingClassDto expected = template.exchange("/api/trainingclasses/" + id,
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

        template.delete("/api/trainingclasses/" + id);


        List<TrainingClassDto> expected = template.exchange("/api/trainingclasses",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TrainingClassDto>>() {})
                .getBody();

        assertEquals(2, expected.size());
        assertThat(expected)
                .extracting(TrainingClassDto::getName)
                .containsExactly("Újratervezés 2.0", "Struktúraváltó alap");
    }

    @Test
    void testCreateTrainingClassWithNullName() {
        Problem expected = template.postForObject("/api/trainingclasses",
                new CreateTrainingClassCommand(null, LocalDate.of(2021, 9, 30)),
                Problem.class);

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }

    @Test
    void testCreateTrainingClassWithEmptyName() {
        Problem expected = template.postForObject("/api/trainingclasses",
                new CreateTrainingClassCommand("  ", LocalDate.of(2021, 9, 30)),
                Problem.class);

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }

    @Test
    void testUpdateTrainingClassWithInvalidEndDate() {

        long id = trainingClass.getId();

        Problem expected = template.exchange("/api/trainingclasses/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(new UpdateTrainingClassCommand("Struktúraváltó haladó Java",
                        new StartEndDates(LocalDate.of(2021, 8, 9),
                                LocalDate.of(2021, 6, 7)))),
                Problem.class)
                .getBody();

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }

    @Test
    void testSetSyllabus() {

        SyllabusDto syllabusDto = template.postForObject("/api/syllabuses", new CreateSyllabusCommand("Java SE"), SyllabusDto.class);

        long syllabusId = syllabusDto.getId();
        long id = trainingClass.getId();

        template.put("/api/trainingclasses/" + id + "/syllabuses",
                new SetSyllabusCommand(syllabusId));

        TrainingClassDto expected = template.exchange("/api/trainingclasses/" + id,
                HttpMethod.GET,
                null,
                TrainingClassDto.class)
                .getBody();

        assertEquals("Java SE", expected.getSyllabus().getName());
    }

    @Test
    void testSetSyllabusWithNullId() {

        SyllabusDto syllabusDto = template.postForObject("/api/syllabuses", new CreateSyllabusCommand("Java SE"), SyllabusDto.class);

        long syllabusId = syllabusDto.getId();
        long id = trainingClass.getId();

        Problem expected = template.exchange("/api/trainingclasses/" + id + "/syllabuses",
                HttpMethod.PUT,
                new HttpEntity<>(new SetSyllabusCommand(null)),
                Problem.class)
                .getBody();

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }
}
