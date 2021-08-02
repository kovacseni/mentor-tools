package mentor.student;

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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(statements = "delete from students")
public class StudentControllerIT {

    StudentDto student;

    @Autowired
    TestRestTemplate template;

    @BeforeEach
    void setUp() {
        template.postForObject("/api/students",
                new CreateStudentCommand("Kiss József", "kissj@gmail.com", "kissj"),
                StudentDto.class);
        template.postForObject("/api/students",
                new CreateStudentCommand("Nagy Béla", "nagyb@gmail.com", "nagyb"),
                StudentDto.class);
        student = template.postForObject("/api/students",
                new CreateStudentCommand("Szép Virág", "szepv@gmail.com"),
                StudentDto.class);
    }

    @Test
    void testListStudents() {

        List<StudentDto> expected = template.exchange("/api/students",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StudentDto>>() {
                })
                .getBody();

        assertThat(expected)
                .extracting(StudentDto::getName)
                .containsExactly("Kiss József", "Nagy Béla", "Szép Virág");
    }

    @Test
    void testFindStudentById() {

        long id = student.getId();

        StudentDto expected = template.exchange("/api/students/" + id,
                HttpMethod.GET,
                null,
                StudentDto.class)
                .getBody();

        assertEquals("Szép Virág", expected.getName());
        assertEquals("szepv@gmail.com", expected.getEmail());
    }

    @Test
    void testUpdateStudent() {

        long id = student.getId();

        template.put("/api/students/" + id, new UpdateStudentCommand("Szép Virág",
                "szepv@yahoo.com", "szepv", "Rendesen halad az ütemezett anyaggal."));

        StudentDto expected = template.exchange("/api/students/" + id,
                HttpMethod.GET,
                null,
                StudentDto.class)
                .getBody();

        assertAll(
                () -> assertEquals("Szép Virág", expected.getName()),
                () -> assertEquals("szepv@yahoo.com", expected.getEmail()),
                () -> assertEquals("szepv", expected.getGitHub()),
                () -> assertEquals("Rendesen halad az ütemezett anyaggal.", expected.getComment()));
    }

    @Test
    void testDeleteStudent() {

        long id = student.getId();

        template.delete("/api/students/" + id);

        List<StudentDto> expected = template.exchange("/api/students",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StudentDto>>() {
                })
                .getBody();

        assertEquals(2, expected.size());
        assertThat(expected)
                .extracting(StudentDto::getName)
                .containsExactly("Kiss József", "Nagy Béla");
    }

    @Test
    void testCreateStudentWithNullName() {

        Problem expected = template.postForObject(
                "/api/students",
                new CreateStudentCommand(null, "kissj@gmail.com", "kissj"),
                Problem.class
        );

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }

    @Test
    void testCreateStudentWithEmptyName() {

        Problem expected = template.postForObject(
                "/api/students",
                new CreateStudentCommand("  ", "kissj@gmail.com", "kissj"),
                Problem.class
        );

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }

    @Test
    void testCreateStudentWithNullEmail() {

        Problem expected = template.postForObject(
                "/api/students",
                new CreateStudentCommand("Kiss József", null, "kissj"),
                Problem.class
        );

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }

    @Test
    void testCreateStudentWithEmptyEmail() {

        Problem expected = template.postForObject(
                "/api/students",
                new CreateStudentCommand("Kiss József", "  ", "kissj"),
                Problem.class
        );

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }

    @Test
    void testUpdateStudentWithNullName() {

        long id = student.getId();

        Problem expected = template.exchange("/api/students/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(new UpdateStudentCommand(null,
                        "szepv@yahoo.com", "szepv",
                        "Rendesen halad az ütemezett anyaggal.")),
                Problem.class)
                .getBody();

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }

    @Test
    void testUpdateStudentWithEmptyName() {

        long id = student.getId();

        Problem expected = template.exchange("/api/students/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(new UpdateStudentCommand(" ",
                        "szepv@yahoo.com", "szepv",
                        "Rendesen halad az ütemezett anyaggal.")),
                Problem.class)
                .getBody();

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }

    @Test
    void testUpdateStudentWithNullEmail() {

        long id = student.getId();

        Problem expected = template.exchange("/api/students/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(new UpdateStudentCommand("Szép Virág",
                        null, "szepv",
                        "Rendesen halad az ütemezett anyaggal.")),
                Problem.class)
                .getBody();

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }

    @Test
    void testUpdateStudentWithEmptyEmail() {

        long id = student.getId();

        Problem expected = template.exchange("/api/students/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(new UpdateStudentCommand("Szép Virág",
                        " ", "szepv",
                        "Rendesen halad az ütemezett anyaggal.")),
                Problem.class)
                .getBody();

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }
}
