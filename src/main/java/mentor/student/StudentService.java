package mentor.student;

import lombok.AllArgsConstructor;
import mentor.registration.Registration;
import mentor.registration.RegistrationRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class StudentService {

    private ModelMapper modelMapper;

    private StudentRepository repository;

    @Autowired
    private RegistrationRepository registrationRepository;

    public List<StudentDto> listStudents(Optional<String> prefix) {
        Type targetListType = new TypeToken<List<StudentDto>>(){}.getType();
        List<Student> filteredStudents = repository.findAll().stream()
                .filter(student -> prefix.isEmpty() || student.getName().toLowerCase().contains(prefix.get().toLowerCase()))
                .collect(Collectors.toList());
        return modelMapper.map(filteredStudents, targetListType);
    }

    public StudentDto findStudentById(long id) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student with id: " + id + " not found."));
        return modelMapper.map(student, StudentDto.class);
    }

    public StudentDto createStudent(CreateStudentCommand command) {
        Student student = new Student(command.getName(), command.getEmail(), command.getGitHub());
        repository.save(student);
        return modelMapper.map(student, StudentDto.class);
    }

    @Transactional
    public StudentDto updateStudent(long id, UpdateStudentCommand command) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student with id: " + id + " not found."));
        student.setName(command.getName());
        student.setEmail(command.getEmail());
        student.setGitHub(command.getGitHub());
        student.setComment(command.getComment());

        return modelMapper.map(student, StudentDto.class);
    }

    @Transactional
    public void deleteStudent(long id) {
        deleteRegistrationsToThisStudent(id);
        repository.deleteById(id);
    }

    private void deleteRegistrationsToThisStudent(long id) {
        List<Registration> registrationsToDelete = registrationRepository.findAll().stream()
                .filter(registration -> registration.getStudent().getId() == id)
                .collect(Collectors.toList());

        registrationRepository.deleteAll(registrationsToDelete);
    }
}
