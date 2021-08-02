package mentor.trainingclass;

import lombok.AllArgsConstructor;
import lombok.Data;
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
                .orElseThrow(() -> new IllegalArgumentException("Training Class with id: " + id + "not found."));
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
                .orElseThrow(() -> new IllegalArgumentException("Training Class with id: " + id + "not found."));
        trainingClass.setName(command.getName());
        trainingClass.setStartDate(command.getDates().getStartDate());
        trainingClass.setEndDate(command.getDates().getEndDate());

        return modelMapper.map(trainingClass, TrainingClassDto.class);
    }

    public void deleteTrainingClass(long id) {
        repository.deleteById(id);
    }

    @Transactional
    public TrainingClassDto addSyllabus(long id, AddSyllabusCommand command) {
        Syllabus syllabus = modelMapper.map(syllabusService.findSyllabusById(command.getSyllabusId()), Syllabus.class);
        TrainingClass trainingClass = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Training Class with id: " + id + "not found."));
        trainingClass.setSyllabus(syllabus);
        syllabus.addTrainingClass(trainingClass);

        return modelMapper.map(trainingClass, TrainingClassDto.class);
    }
}
