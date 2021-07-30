package mentor.student;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private StudentService service;

    @GetMapping
    public List<StudentDto> listStudents(@RequestParam Optional<String> prefix) {
        return service.listStudents(prefix);
    }

    @GetMapping("/{id}")
    public StudentDto findStudentById(@PathVariable("id") long id) {
        return service.findStudentById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentDto createStudent(@Valid @RequestBody CreateStudentCommand command) {
        return service.createStudent(command);
    }

    @PutMapping("/{id}")
    public StudentDto updateStudent(@PathVariable("id") long id, @Valid @RequestBody UpdateStudentCommand command) {
        return service.updateStudent(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable("id") long id) {
        service.deleteStudent(id);
    }
}
