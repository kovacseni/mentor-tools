package mentor.registration;

import mentor.student.CreateStudentCommand;
import mentor.student.StudentController;
import mentor.student.StudentDto;
import mentor.trainingclass.CreateTrainingClassCommand;
import mentor.trainingclass.TrainingClassController;
import mentor.trainingclass.TrainingClassDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.jdbc.Sql;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(statements = "delete from registrations")
public class RegistrationControllerIT {

    RegistrationDto registration;

    TrainingClassDto trainingClass;

    StudentDto student;

    @Autowired
    TestRestTemplate template;

    @Autowired
    TrainingClassController trainingClassController;

    @Autowired
    StudentController studentController;

    @BeforeEach
    void setUp() {
        trainingClass = template.postForObject("/api/trainingclasses",
                new CreateTrainingClassCommand("Struktúraváltó haladó",
                        LocalDate.of(2021, 6, 7)),
                TrainingClassDto.class);
        StudentDto student1 = template.postForObject("/api/students",
                new CreateStudentCommand("Szép Virág", "szepv@gmail.com"),
                StudentDto.class);

        long trainingClassId1 = trainingClass.getId();
        long studentId1 = student1.getId();

        registration = template.postForObject("/api/trainingclasses/" + trainingClassId1 + "/registrations",
                new CreateRegistrationCommand(studentId1), RegistrationDto.class);

        TrainingClassDto trainingClass2 = template.postForObject("/api/trainingclasses",
                new CreateTrainingClassCommand("Struktúraváltó alap",
                        LocalDate.of(2020, 10, 28)),
                TrainingClassDto.class);
        student = template.postForObject("/api/students",
                new CreateStudentCommand("Nagy Béla", "nagyb@gmail.com", "nagyb"),
                StudentDto.class);

        long trainingClassId2 = trainingClass2.getId();
        long studentId2 = student.getId();

        template.postForObject("/api/trainingclasses/" + trainingClassId2 + "/registrations",
                new CreateRegistrationCommand(studentId2), RegistrationDto.class);

        template.postForObject("/api/trainingclasses/" + trainingClassId1 + "/registrations",
                new CreateRegistrationCommand(studentId2), RegistrationDto.class);
    }

    @Test
    void testListRegistrations() {

        List<RegistrationDto> expected = template.exchange("/api/registrations",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<RegistrationDto>>() {
                })
                .getBody();

        assertEquals(3, expected.size());
    }

    @Test
    void testFindRegistrationById() {

        long id = registration.getId();

        RegistrationDto expected = template.exchange("/api/registrations/" + id,
                HttpMethod.GET,
                null,
                RegistrationDto.class)
                .getBody();

        assertAll(
                () -> assertEquals("Struktúraváltó haladó", expected.getTrainingClass().getName()),
                () -> assertEquals(LocalDate.of(2021, 6, 7), expected.getTrainingClass().getStartDate()),
                () -> assertEquals("Szép Virág", expected.getStudent().getName()),
                () -> assertEquals("szepv@gmail.com", expected.getStudent().getEmail()),
                () -> assertEquals(RegistrationStatus.ACTIVE, expected.getStatus()));
    }

    @Test
    void testGetRegistrationsByTrainingClassId() {

        long trainingClassId = trainingClass.getId();

        List<TrainingClassRegistrationDto> expected = template.exchange(
                "/api/trainingclasses/" + trainingClassId + "/registrations",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TrainingClassRegistrationDto>>() {
                })
                .getBody();

        assertEquals(2, expected.size());
        assertThat(expected)
                .extracting(TrainingClassRegistrationDto::getStudentName)
                .containsExactly("Szép Virág", "Nagy Béla");
    }

    @Test
    void testGetRegistrationByStudentId() {

        long id = student.getId();

        List<StudentRegistrationDto> expected = template.exchange(
                "/api/students/" + id + "/registrations",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StudentRegistrationDto>>() {
                })
                .getBody();

        assertEquals(2, expected.size());
        assertThat(expected)
                .extracting(StudentRegistrationDto::getTrainingClassName)
                .containsExactly("Struktúraváltó alap", "Struktúraváltó haladó");
    }

    @Test
    void testUpdateRegistration() {

        long id = registration.getId();

        template.put("/api/registrations/" + id, new UpdateRegistrationCommand(RegistrationStatus.EXITED));

        RegistrationDto expected = template.exchange("/api/registrations/" + id,
                HttpMethod.GET,
                null,
                RegistrationDto.class)
                .getBody();

        assertAll(
                () -> assertEquals("Struktúraváltó haladó", expected.getTrainingClass().getName()),
                () -> assertEquals(LocalDate.of(2021, 6, 7), expected.getTrainingClass().getStartDate()),
                () -> assertEquals("Szép Virág", expected.getStudent().getName()),
                () -> assertEquals("szepv@gmail.com", expected.getStudent().getEmail()),
                () -> assertEquals(RegistrationStatus.EXITED, expected.getStatus()));
    }

    @Test
    void testDeleteRegistration() {

        long id = registration.getId();

        template.delete("/api/registrations/" + id);

        List<RegistrationDto> expected = template.exchange("/api/registrations",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<RegistrationDto>>() {
                })
                .getBody();

        assertEquals(2, expected.size());
    }

    @Test
    void testCreateRegistrationWithNullStudentId() {

        long trainingClassId = trainingClass.getId();

        Problem expected =  template.postForObject("/api/trainingclasses/" + trainingClassId + "/registrations",
                new CreateRegistrationCommand(null), Problem.class);

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }

    @Test
    void testUpdateRegistrationWithWrongStatus1() {

        long id = registration.getId();

        template.put("/api/registrations/" + id, new UpdateRegistrationCommand(RegistrationStatus.EXIT_IN_PROGRESS));

        template.put("/api/registrations/" + id, new UpdateRegistrationCommand(RegistrationStatus.ACTIVE));

        RegistrationDto expected = template.exchange("/api/registrations/" + id,
                HttpMethod.GET,
                null,
                RegistrationDto.class)
                .getBody();

        assertAll(
                () -> assertEquals("Struktúraváltó haladó", expected.getTrainingClass().getName()),
                () -> assertEquals(LocalDate.of(2021, 6, 7), expected.getTrainingClass().getStartDate()),
                () -> assertEquals("Szép Virág", expected.getStudent().getName()),
                () -> assertEquals("szepv@gmail.com", expected.getStudent().getEmail()),
                () -> assertEquals(RegistrationStatus.EXIT_IN_PROGRESS, expected.getStatus()));
    }

    @Test
    void testUpdateRegistrationWithWrongStatus2() {

        long id = registration.getId();

        template.put("/api/registrations/" + id, new UpdateRegistrationCommand(RegistrationStatus.EXITED));

        template.put("/api/registrations/" + id, new UpdateRegistrationCommand(RegistrationStatus.ACTIVE));

        RegistrationDto expected = template.exchange("/api/registrations/" + id,
                HttpMethod.GET,
                null,
                RegistrationDto.class)
                .getBody();

        assertAll(
                () -> assertEquals("Struktúraváltó haladó", expected.getTrainingClass().getName()),
                () -> assertEquals(LocalDate.of(2021, 6, 7), expected.getTrainingClass().getStartDate()),
                () -> assertEquals("Szép Virág", expected.getStudent().getName()),
                () -> assertEquals("szepv@gmail.com", expected.getStudent().getEmail()),
                () -> assertEquals(RegistrationStatus.EXITED, expected.getStatus()));
    }

    @Test
    void testUpdateRegistrationWithWrongStatus3() {

        long id = registration.getId();

        template.put("/api/registrations/" + id, new UpdateRegistrationCommand(RegistrationStatus.EXITED));

        template.put("/api/registrations/" + id, new UpdateRegistrationCommand(RegistrationStatus.EXIT_IN_PROGRESS));

        RegistrationDto expected = template.exchange("/api/registrations/" + id,
                HttpMethod.GET,
                null,
                RegistrationDto.class)
                .getBody();

        assertAll(
                () -> assertEquals("Struktúraváltó haladó", expected.getTrainingClass().getName()),
                () -> assertEquals(LocalDate.of(2021, 6, 7), expected.getTrainingClass().getStartDate()),
                () -> assertEquals("Szép Virág", expected.getStudent().getName()),
                () -> assertEquals("szepv@gmail.com", expected.getStudent().getEmail()),
                () -> assertEquals(RegistrationStatus.EXITED, expected.getStatus()));
    }
}
