package mentor.registration;

import lombok.AllArgsConstructor;
import mentor.student.Student;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping
public class RegistrationController {

    private  RegistrationService service;

    @GetMapping("/api/registrations")
    public List<RegistrationDto> listRegistrations(@RequestParam Optional<String> prefix, @RequestParam Optional<RegistrationStatus> status) {
        return service.listRegistrations(prefix, status);
    }

    @GetMapping("/api/registrations/{id}")
    public RegistrationDto findRegistrationById(@PathVariable("id") long id) {
        return service.findRegistrationById(id);
    }

    @PostMapping("/api/trainingclasses/{id}/registrations")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationDto createRegistration(@PathVariable("id") long id, @Valid @RequestBody CreateRegistrationCommand command) {
        return service.createRegistration(command);
    }

    @GetMapping("/api/trainingclasses/{id}/registrations")
    public List<TrainingClassRegistrationDto> getRegistrationsByTrainingClassId(@PathVariable("id") long id) {
        return service.getRegistrationsByTrainingClassId(id);
    }

    @GetMapping("/api/students/{id}/registrations")
    public List<StudentRegistrationDto> getRegistrationByStudentId(@PathVariable("id") long id) {
        return service.getRegistrationsByStudentId(id);
    }

    @PutMapping("/api/registrations/{id}")
    public RegistrationDto updateRegistration(@PathVariable("id") long id, @Valid @RequestBody UpdateRegistrationCommand command) {
        return service.updateRegistration(id, command);
    }

    @DeleteMapping("/api/registrations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRegistration(@PathVariable("id") long id) {
        service.deleteRegistration(id);
    }
}
