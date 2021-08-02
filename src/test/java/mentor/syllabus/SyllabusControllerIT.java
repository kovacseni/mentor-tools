package mentor.syllabus;

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
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(statements = "delete from syllabuses")
public class SyllabusControllerIT {

    SyllabusDto syllabus;

    @Autowired
    TestRestTemplate template;

    @BeforeEach
    void setUp() {
        template.postForObject("/api/syllabuses", new CreateSyllabusCommand("Java SE"), SyllabusDto.class);
        template.postForObject("/api/syllabuses", new CreateSyllabusCommand("JDBC"), SyllabusDto.class);
        syllabus = template.postForObject("/api/syllabuses", new CreateSyllabusCommand("JPA"), SyllabusDto.class);
    }

    @Test
    void testListSyllabuses() {
        List<SyllabusDto> expected = template.exchange("/api/syllabuses",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SyllabusDto>>() {
                })
                .getBody();

        assertEquals(3, expected.size());
        assertThat(expected)
                .extracting(SyllabusDto::getName)
                .containsExactly("Java SE", "JDBC", "JPA");
    }

    @Test
    void testListSyllabusesByPrefix() {
        List<SyllabusDto> expected = template.exchange("/api/syllabuses?prefix=java",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SyllabusDto>>() {
                })
                .getBody();

        assertEquals(1, expected.size());
        assertThat(expected)
                .extracting(SyllabusDto::getName)
                .containsExactly("Java SE");
    }

    @Test
    void testFindSyllabusById() {

        long id = syllabus.getId();

        SyllabusDto expected = template.exchange("/api/syllabuses/" + id,
                HttpMethod.GET,
                null,
                SyllabusDto.class)
                .getBody();

        assertEquals("JPA", expected.getName());
    }

    @Test
    void testUpdateSyllabus() {

        long id = syllabus.getId();

        template.put("/api/syllabuses/" + id, new UpdateSyllabusCommand("Adatbázis programozás Java nyelven - JPA"));

        SyllabusDto expected = template.exchange("/api/syllabuses/" + id,
                HttpMethod.GET,
                null,
                SyllabusDto.class)
                .getBody();

        assertEquals("Adatbázis programozás Java nyelven - JPA", expected.getName());
    }

    @Test
    void testDeleteSyllabus() {

        long id = syllabus.getId();

        template.delete("/api/syllabuses/" + id);

        List<SyllabusDto> expected = template.exchange("/api/syllabuses",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SyllabusDto>>() {
                })
                .getBody();

        assertEquals(2, expected.size());
        assertThat(expected)
                .extracting(SyllabusDto::getName)
                .containsExactly("Java SE", "JDBC");
    }

    @Test
    void testCreateSyllabusWithNullName() {

        Problem expected = template.postForObject("/api/syllabuses", new CreateSyllabusCommand(null), Problem.class);

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }

    @Test
    void testCreateSyllabusWithEmptyName() {

        Problem expected = template.postForObject("/api/syllabuses", new CreateSyllabusCommand("  "), Problem.class);

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }

    @Test
    void testUpdateSyllabusWithNullName() {

        long id = syllabus.getId();

        Problem expected = template.exchange("/api/syllabuses/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(new UpdateSyllabusCommand(null)),
                Problem.class)
                .getBody();

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }

    @Test
    void testUpdateSyllabusWithEmptyName() {

        long id = syllabus.getId();

        Problem expected = template.exchange("/api/syllabuses/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(new UpdateSyllabusCommand("  ")),
                Problem.class)
                .getBody();

        assertEquals(Status.BAD_REQUEST, expected.getStatus());
    }
}
