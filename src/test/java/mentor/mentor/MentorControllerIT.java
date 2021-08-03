package mentor.mentor;

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
@Sql(statements = "delete from mentors")
public class MentorControllerIT {

    MentorDto mentor;

    @Autowired
    TestRestTemplate template;

    @BeforeEach
    void setUp() {
        template.postForObject("/api/mentors",
                new CreateMentorCommand("Mentor Trainer", "trainer.mentor@training.com"),
                MentorDto.class);
        template.postForObject("/api/mentors",
                new CreateMentorCommand("Instructor Mentor", "mentor.instructor@training.com"),
                MentorDto.class);
        mentor = template.postForObject("/api/mentors",
                new CreateMentorCommand("Trainer Instructor", "instructor.trainer@training.com"),
                MentorDto.class);
    }

    @Test
    void testListMentors() {

        List<MentorDto> expected = template.exchange("/api/mentors",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<MentorDto>>() {
                })
                .getBody();

        assertEquals(3, expected.size());
        assertThat(expected)
                .extracting(MentorDto::getName)
                .containsExactly("Mentor Trainer", "Instructor Mentor", "Trainer Instructor");
    }

    @Test
    void testListMentorsByPrefix() {

        List<MentorDto> expected = template.exchange("/api/mentors?prefix=train",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<MentorDto>>() {
                })
                .getBody();

        assertEquals(2, expected.size());
        assertThat(expected)
                .extracting(MentorDto::getName)
                .containsExactly("Mentor Trainer", "Trainer Instructor");
    }

    @Test
    void testFindMentorById() {

        long id = mentor.getId();

        MentorDto expected = template.exchange("/api/mentors/" + id,
                HttpMethod.GET,
                null,
                MentorDto.class)
                .getBody();

        assertEquals("Trainer Instructor", expected.getName());
        assertEquals("instructor.trainer@training.com", expected.getEmail());
    }

    @Test
    void testUpdateMentor() {

        long id = mentor.getId();

        template.put("/api/mentors/" + id, new UpdateMentorCommand("Mentor Trainer Instructor",
                "mentort@gmail.com", Position.JUNIOR_OKTATO));

        MentorDto expected = template.exchange("/api/mentors/" + id,
                HttpMethod.GET,
                null,
                MentorDto.class)
                .getBody();

        assertAll(
                () -> assertEquals("Mentor Trainer Instructor", expected.getName()),
                () -> assertEquals("mentort@gmail.com", expected.getEmail()),
                () -> assertEquals(Position.JUNIOR_OKTATO, expected.getPosition()));
    }

    @Test
    void testDeleteMentor() {

        long id = mentor.getId();

        template.delete("/api/mentors/" + id);

        List<MentorDto> expected = template.exchange("/api/mentors",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<MentorDto>>() {
                })
                .getBody();

        assertEquals(2, expected.size());
        assertThat(expected)
                .extracting(MentorDto::getName)
                .containsExactly("Mentor Trainer", "Instructor Mentor");
    }

    @Test
    void testCreateMentorWithNullName() {

        Problem expected = template.postForObject(
                "/api/mentors",
                new CreateMentorCommand(null, "trainer.mentor@training.com"),
                Problem.class
        );

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }

    @Test
    void testCreateMentorWithEmptyName() {

        Problem expected = template.postForObject(
                "/api/mentors",
                new CreateMentorCommand("   ", "trainer.mentor@training.com"),
                Problem.class
        );

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }

    @Test
    void testCreateMentorWithNullEmail() {

        Problem expected = template.postForObject(
                "/api/mentors",
                new CreateMentorCommand("Mentor Trainer", null),
                Problem.class
        );

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }

    @Test
    void testCreateMentorWithEmptyEmail() {

        Problem expected = template.postForObject(
                "/api/mentors",
                new CreateMentorCommand("Mentor Trainer", "  "),
                Problem.class
        );

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }

    @Test
    void testUpdateMentorWithNullName() {

        long id = mentor.getId();

        Problem expected = template.exchange("/api/mentors/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(new UpdateMentorCommand(null,
                        "trainer.mentor@training.com")),
                Problem.class)
                .getBody();

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }

    @Test
    void testUpdateMentorWithEmptyName() {

        long id = mentor.getId();

        Problem expected = template.exchange("/api/mentors/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(new UpdateMentorCommand("   ",
                        "trainer.mentor@training.com")),
                Problem.class)
                .getBody();

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }

    @Test
    void testUpdateMentorWithNullEmail() {

        long id = mentor.getId();

        Problem expected = template.exchange("/api/mentors/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(new UpdateMentorCommand("Mentor Trainer",
                        null)),
                Problem.class)
                .getBody();

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }

    @Test
    void testUpdateMentorWithEmptyEmail() {

        long id = mentor.getId();

        Problem expected = template.exchange("/api/mentors/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(new UpdateMentorCommand("Mentor Trainer",
                        "   ")),
                Problem.class)
                .getBody();

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }
}
