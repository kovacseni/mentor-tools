package mentor.trainingclass;

import lombok.AllArgsConstructor;
import lombok.Data;
import mentor.registration.*;
import mentor.syllabus.Syllabus;
import mentor.syllabus.SyllabusService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Service
public class TrainingClassService {

    private ModelMapper modelMapper;

    private TrainingClassRepository repository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private SyllabusService syllabusService;

    public List<TrainingClassDto> listTrainingClasses(Optional<String> prefix) {
        Type targetListType = new TypeToken<List<TrainingClassDto>>(){}.getType();
        List<TrainingClass> filteredTrainingClasses = repository.findAll().stream()
                .filter(trainingClass -> prefix.isEmpty() || trainingClass.getName().toLowerCase().contains(prefix.get().toLowerCase()))
                .collect(Collectors.toList());
        return modelMapper.map(filteredTrainingClasses, targetListType);
    }

    public TrainingClassDto findTrainingClassById(long id) {
        TrainingClass trainingClass = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Training Class with id: " + id + " not found."));
        return modelMapper.map(trainingClass, TrainingClassDto.class);
    }

    public TrainingClassDto createTrainingClass(CreateTrainingClassCommand command) {
        TrainingClass trainingClass = new TrainingClass(command.getName(), command.getStartDate());
        repository.save(trainingClass);
        return modelMapper.map(trainingClass, TrainingClassDto.class);
    }

    @Transactional
    public TrainingClassDto updateTrainingClass(long id, UpdateTrainingClassCommand command) {
        TrainingClass trainingClass = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Training Class with id: " + id + " not found."));
        trainingClass.setName(command.getName());
        trainingClass.setStartDate(command.getDates().getStartDate());
        trainingClass.setEndDate(command.getDates().getEndDate());

        return modelMapper.map(trainingClass, TrainingClassDto.class);
    }

    @Transactional
    public void deleteTrainingClass(long id) {
        deleteRegistrationsToThisTrainingClass(id);
        repository.deleteById(id);
    }

    private void deleteRegistrationsToThisTrainingClass(long id) {
        List<Registration> registrationsToDelete = registrationRepository.findAll().stream()
                .filter(registration -> registration.getTrainingClass().getId() == id)
                .collect(Collectors.toList());

      registrationRepository.deleteAll(registrationsToDelete);
    }

    @Transactional
    public TrainingClassDto setSyllabus(long id, SetSyllabusCommand command) {
        Syllabus syllabus = modelMapper.map(syllabusService.findSyllabusById(command.getSyllabusId()), Syllabus.class);
        TrainingClass trainingClass = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Training Class with id: " + id + " not found."));
        trainingClass.setSyllabus(syllabus);

        return modelMapper.map(trainingClass, TrainingClassDto.class);
    }
}
