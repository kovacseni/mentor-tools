package mentor.controller;

import lombok.AllArgsConstructor;
import mentor.entity.CreateTrainingClassCommand;
import mentor.entity.TrainingClassDto;
import mentor.entity.UpdateTrainingClassCommand;
import mentor.service.TrainingClassService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/trainingclass")
public class TrainingClassController {

    private TrainingClassService service;

    @GetMapping
    public List<TrainingClassDto> listTrainingClasses(@RequestParam Optional<String> prefix) {
        return service.listTrainingClasses(prefix);
    }

    @GetMapping("/{id}")
    public TrainingClassDto findTrainingClassById(@PathVariable("id") long id) {
        return service.findTrainingClassById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrainingClassDto createTrainingClass(@Valid @RequestBody CreateTrainingClassCommand command) {
        return service.createTrainingClass(command);
    }

    @PutMapping("/{id}")
    public TrainingClassDto updateTrainingClass(@PathVariable("id") long id, @Valid @RequestBody UpdateTrainingClassCommand command) {
        return service.updateTrainingClass(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrainingClass(@PathVariable("id") long id) {
        service.deleteTrainingClass(id);
    }
}
