package mentor.mentor;

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
public class MentorService {

    private ModelMapper modelMapper;

    private MentorRepository repository;

    public List<MentorDto> listMentors(Optional<String> prefix) {
        Type targetListType = new TypeToken<List<MentorDto>>(){}.getType();
        List<Mentor> filteredMentors = repository.findAll().stream()
                .filter(mentor -> prefix.isEmpty() || mentor.getName().toLowerCase().contains(prefix.get().toLowerCase()))
                .collect(Collectors.toList());
        return modelMapper.map(filteredMentors, targetListType);
    }

    public MentorDto findMentorById(long id) {
        Mentor mentor = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mentor with id: " + id + " not found."));
        return modelMapper.map(mentor, MentorDto.class);
    }

    public MentorDto createMentor(CreateMentorCommand command) {
        Mentor mentor = new Mentor(command.getName(), command.getEmail());
        repository.save(mentor);
        return modelMapper.map(mentor, MentorDto.class);
    }

    @Transactional
    public MentorDto updateMentor(long id, UpdateMentorCommand command) {
        Mentor mentor = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mentor with id: " + id + " not found."));
        mentor.setName(command.getName());
        mentor.setEmail(command.getEmail());
        if (command.getPosition() != null) {
            mentor.setPosition(command.getPosition());
        }

        return modelMapper.map(mentor, MentorDto.class);
    }

    public void deleteMentor(long id) {
        repository.deleteById(id);
    }
}
