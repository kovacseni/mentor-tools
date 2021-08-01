package mentor.syllabus;

import lombok.AllArgsConstructor;
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
public class SyllabusService {

    private ModelMapper modelMapper;

    private SyllabusRepository repository;

    public List<SyllabusDto> listSyllabuses(Optional<String> prefix) {
        Type targetListType = new TypeToken<List<SyllabusDto>>() {}.getType();
        List<Syllabus> filteredSyllabuses = repository.findAll().stream()
                .filter(student -> prefix.isEmpty() || student.getName().toLowerCase().contains(prefix.get().toLowerCase()))
                .collect(Collectors.toList());
        return modelMapper.map(filteredSyllabuses, targetListType);
    }

    public SyllabusDto findSyllabusById(long id) {
        Syllabus syllabus = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Syllabus with id: " + id + "not found."));
        return modelMapper.map(syllabus, SyllabusDto.class);
    }

    public SyllabusDto createSyllabus(CreateSyllabusCommand command) {
        Syllabus syllabus = new Syllabus(command.getName());
        repository.save(syllabus);
        return modelMapper.map(syllabus, SyllabusDto.class);
    }

    @Transactional
    public SyllabusDto updateSyllabus(long id, UpdateSyllabusCommand command) {
        Syllabus syllabus = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Syllabus with id: " + id + "not found."));
        syllabus.setName(command.getName());

        return modelMapper.map(syllabus, SyllabusDto.class);
    }

    public void deleteSyllabus(long id) {
        repository.deleteById(id);
    }
}
