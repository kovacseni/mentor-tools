package mentor.service;

import lombok.AllArgsConstructor;
import mentor.entity.CreateTrainingClassCommand;
import mentor.entity.TrainingClass;
import mentor.entity.TrainingClassDto;
import mentor.entity.UpdateTrainingClassCommand;
import mentor.repository.TrainingClassRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TrainingClassService {

    private ModelMapper modelMapper;

    private TrainingClassRepository repository;

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
        trainingClass.setStartDate(command.getStartDate());
        trainingClass.setEndDate(command.getEndDate());

        return modelMapper.map(trainingClass, TrainingClassDto.class);
    }

    public void deleteTrainingClass(long id) {
        repository.deleteById(id);
    }
}
