package mentor.mentor;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/mentors")
public class MentorController {

    private MentorService service;

    @GetMapping
    public List<MentorDto> listMentors(@RequestParam Optional<String> prefix) {
        return service.listMentors(prefix);
    }

    @GetMapping("/{id}")
    public MentorDto findMentorById(@PathVariable("id") long id) {
        return service.findMentorById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MentorDto createMentor(@Valid @RequestBody CreateMentorCommand command) {
        return service.createMentor(command);
    }

    @PutMapping("/{id}")
    public MentorDto updateMentor(@PathVariable("id") long id, @Valid @RequestBody UpdateMentorCommand command) {
        return service.updateMentor(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMentor(@PathVariable("id") long id) {
        service.deleteMentor(id);
    }
}
