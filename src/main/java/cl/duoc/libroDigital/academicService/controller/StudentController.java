package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.model.Student;
import cl.duoc.libroDigital.academicService.service.StudentService;
import cl.duoc.libroDigital.academicService.dto.StudentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // ===== Mapper: Entity -> DTO =====
    private StudentDTO toDTO(Student student) {
        StudentDTO dto = new StudentDTO();

        dto.setId(student.getId());
        dto.setRut(student.getRut());

        dto.setFirstName(student.getFirstName());
        dto.setSecondName(student.getSecondName());
        dto.setLastName(student.getLastName());
        dto.setMotherLastName(student.getMotherLastName());

        dto.setEmail(student.getEmail());
        dto.setPhone(student.getPhone());
        dto.setAddress(student.getAddress());
        dto.setCommune(student.getCommune());
        dto.setCity(student.getCity());

        dto.setDateOfBirth(student.getDateOfBirth());
        dto.setAdmissionDate(student.getAdmissionDate());

        dto.setGuardianId(student.getGuardianId());
        dto.setUserId(student.getUserId());

        dto.setCreatedAt(student.getCreatedAt());
        dto.setUpdatedAt(student.getUpdatedAt());

        dto.setStudentStatus(student.getStudentStatus());

        return dto;
    }

    // ===== Mapper: DTO -> Entity =====
    private Student toEntity(StudentDTO dto) {
        Student student = new Student();

        student.setId(dto.getId());
        student.setRut(dto.getRut());

        student.setFirstName(dto.getFirstName());
        student.setSecondName(dto.getSecondName());
        student.setLastName(dto.getLastName());
        student.setMotherLastName(dto.getMotherLastName());

        student.setEmail(dto.getEmail());
        student.setPhone(dto.getPhone());
        student.setAddress(dto.getAddress());
        student.setCommune(dto.getCommune());
        student.setCity(dto.getCity());

        student.setDateOfBirth(dto.getDateOfBirth());
        student.setAdmissionDate(dto.getAdmissionDate());

        student.setGuardianId(dto.getGuardianId());
        student.setUserId(dto.getUserId());

        student.setStudentStatus(dto.getStudentStatus());

        return student;
    }

    // ===== Endpoints =====

    @PostMapping
    public StudentDTO createStudent(@RequestBody StudentDTO dto) {
        Student created = studentService.createStudent(toEntity(dto));
        return toDTO(created);
    }

    @GetMapping
    public List<StudentDTO> getAllStudents() {
        return studentService.getAllStudents()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public StudentDTO getStudent(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    @PutMapping("/{id}")
    public StudentDTO updateStudent(@PathVariable Long id, @RequestBody StudentDTO dto) {
        Student updated = studentService.updateStudent(id, toEntity(dto));
        return toDTO(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }
}

