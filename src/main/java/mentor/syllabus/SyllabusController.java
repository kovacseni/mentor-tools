package mentor.syllabus;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/syllabuses")
public class SyllabusController {

    private SyllabusService service;

    @GetMapping
    public List<SyllabusDto> listSyllabuses(@RequestParam Optional<String> prefix) {
        return service.listSyllabuses(prefix);
    }

    @GetMapping("/{id}")
    public SyllabusDto findSyllabusById(@PathVariable("id") long id) {
        return service.findSyllabusById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SyllabusDto createSyllabus(@Valid @RequestBody CreateSyllabusCommand command) {
        return service.createSyllabus(command);
    }

    @PutMapping("/{id}")
    public SyllabusDto updateSyllabus(@PathVariable("id") long id, @Valid @RequestBody UpdateSyllabusCommand command) {
        return service.updateSyllabus(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSyllabus(@PathVariable("id") long id) {
        service.deleteSyllabus(id);
    }
}
