package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.model.Student;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
import cl.duoc.libroDigital.academicService.service.StudentService;
import cl.duoc.libroDigital.academicService.dto.StudentDTO;
import cl.duoc.libroDigital.academicService.security.AcademicAccessService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final CatalogLookupService catalogs;
    private final AcademicAccessService access;

    public StudentController(
            StudentService studentService,
            CatalogLookupService catalogs,
            AcademicAccessService access) {
        this.studentService = studentService;
        this.catalogs = catalogs;
        this.access = access;
    }

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
        dto.setWithdrawalDate(student.getWithdrawalDate());
        dto.setEnrollmentNumber(student.getEnrollmentNumber());
        dto.setGuardianId(student.getGuardianId());
        dto.setUserId(student.getUserId());
        dto.setCreatedAt(student.getCreatedAt());
        dto.setUpdatedAt(student.getUpdatedAt());
        dto.setStudentStatus(catalogs.code("student_statuses", student.getStudentStatusId()));
        return dto;
    }

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
        student.setWithdrawalDate(dto.getWithdrawalDate());
        student.setEnrollmentNumber(dto.getEnrollmentNumber());
        student.setGuardianId(dto.getGuardianId());
        student.setUserId(dto.getUserId());
        student.setStudentStatusId(catalogs.requireId("student_statuses", dto.getStudentStatus()));
        return student;
    }

    @PostMapping
    public StudentDTO createStudent(@RequestBody StudentDTO dto) {
        access.requireAdmin();
        Student created = studentService.createStudent(toEntity(dto));
        return toDTO(created);
    }

    @GetMapping("/me")
    public StudentDTO getCurrentStudent() {
        return access.currentStudent()
                .map(this::toDTO)
                .orElse(null);
    }

    @GetMapping
    public List<StudentDTO> getAllStudents() {
        if (access.isAdmin()) {
            return studentService.getAllStudents().stream().map(this::toDTO).collect(Collectors.toList());
        }
        if (access.isStudent()) {
            return access.currentStudent()
                    .map(student -> List.of(toDTO(student)))
                    .orElse(List.of());
        }
        if (access.isGuardian()) {
            Long guardianId = access.requireGuardianId();
            return studentService.getAllStudents().stream()
                    .filter(student -> guardianId.equals(student.getGuardianId()))
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }
        Long teacherId = access.requireTeacherId();
        Set<Long> allowed = access.teacherStudentIds(teacherId);
        return studentService.getAllStudents().stream()
                .filter(student -> allowed.contains(student.getId()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public StudentDTO getStudent(@PathVariable Long id) {
        access.ensureCanReadStudent(id);
        return studentService.getStudentById(id).map(this::toDTO).orElse(null);
    }

    @PutMapping("/{id}")
    public StudentDTO updateStudent(@PathVariable Long id, @RequestBody StudentDTO dto) {
        access.requireAdmin();
        Student updated = studentService.updateStudent(id, toEntity(dto));
        return toDTO(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        access.requireSuperAdmin();
        studentService.deleteStudent(id);
    }
}
