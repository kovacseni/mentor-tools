package mentor.registration;

import lombok.AllArgsConstructor;
import mentor.student.Student;
import mentor.student.StudentService;
import mentor.trainingclass.TrainingClass;
import mentor.trainingclass.TrainingClassService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.lang.reflect.Type;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class RegistrationService {

    private RegistrationRepository repository;

    private ModelMapper modelMapper;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TrainingClassService trainingClassService;

    public List<RegistrationDto> listRegistrations(Optional<String> prefix, Optional<RegistrationStatus> status) {
        Type targetListType = new TypeToken<List<RegistrationDto>>(){}.getType();
        List<Registration> filteredRegistrations = repository.findAll().stream()
                .filter(registration -> prefix.isEmpty()
                        || registration.getTrainingClass().getName().toLowerCase().contains(prefix.get().toLowerCase())
                        || registration.getStudent().getName().toLowerCase().contains(prefix.get().toLowerCase()))
                .filter(registration -> status.isEmpty()
                        || status.get() == registration.getStatus())
                .collect(Collectors.toList());
        return modelMapper.map(filteredRegistrations, targetListType);
    }

    public RegistrationDto findRegistrationById(long id) {
        Registration registration = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registration with id: " + id + "not found."));
        return modelMapper.map(registration, RegistrationDto.class);
    }

    public RegistrationDto createRegistration(long studentId, CreateRegistrationCommand command) {
        Student student = modelMapper.map(studentService.findStudentById(studentId), Student.class);
        TrainingClass trainingClass = modelMapper.map(trainingClassService.findTrainingClassById(command.getTrainingClassId()), TrainingClass.class);

        Registration registration = new Registration(trainingClass, student, RegistrationStatus.ACTIVE);
        repository.save(registration);
        return modelMapper.map(registration, RegistrationDto.class);
    }

    public List<TrainingClassRegistrationDto> getRegistrationsByTrainingClassId(long id) {
        List<Registration> registrations = repository.findAll().stream()
                .filter(registration -> registration.getTrainingClass().getId() == id)
                .collect(Collectors.toList());
        return getRegistrationsInTrainingClass(registrations);
    }

    private List<TrainingClassRegistrationDto> getRegistrationsInTrainingClass(List<Registration> registrations) {
        List<TrainingClassRegistrationDto> registrationsInTrainingClass = new ArrayList<>();
        for(Registration r : registrations) {
            TrainingClassRegistrationDto registration = new TrainingClassRegistrationDto(
                    r.getTrainingClass(),
                    r.getStudent().getId(),
                    r.getStudent().getName(),
                    r.getStatus()
            );
            registrationsInTrainingClass.add(registration);
        }
        return registrationsInTrainingClass;
    }

    public List<StudentRegistrationDto> getRegistrationsByStudentId(long id) {
        List<Registration> registrations = repository.findAll().stream()
                .filter(registration -> registration.getStudent().getId() == id)
                .collect(Collectors.toList());
        return getRegistrationsOfStudent(registrations);
    }

    private List<StudentRegistrationDto> getRegistrationsOfStudent(List<Registration> registrations) {
        List<StudentRegistrationDto> registrationsOfStudent = new ArrayList<>();
        for(Registration r : registrations) {
            StudentRegistrationDto registration = new StudentRegistrationDto(
                    r.getStudent(),
                    r.getTrainingClass().getId(),
                    r.getTrainingClass().getName(),
                    r.getStatus()
            );
            registrationsOfStudent.add(registration);
        }
        return registrationsOfStudent;
    }

    @Transactional
    public RegistrationDto updateRegistration(long id, UpdateRegistrationCommand command) {
        Registration registration = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registration with id: " + id + "not found."));
        if (registration.getStatus() == RegistrationStatus.ACTIVE) {
            registration.setStatus(command.getStatus());
        } else if (registration.getStatus() == RegistrationStatus.EXIT_IN_PROGRESS
                && command.getStatus() != RegistrationStatus.ACTIVE) {
            registration.setStatus(command.getStatus());
        }
        return modelMapper.map(registration, RegistrationDto.class);
    }

    public void deleteRegistration(long id) {
        repository.deleteById(id);
    }
}
