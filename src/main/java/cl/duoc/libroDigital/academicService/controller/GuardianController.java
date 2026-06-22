package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.dto.GuardianDTO;
import cl.duoc.libroDigital.academicService.dto.StudentDTO;
import cl.duoc.libroDigital.academicService.model.Guardian;
import cl.duoc.libroDigital.academicService.model.Student;
import cl.duoc.libroDigital.academicService.repository.StudentRepository;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
import cl.duoc.libroDigital.academicService.service.GuardianService;
import cl.duoc.libroDigital.academicService.security.AcademicAccessService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/guardians")
public class GuardianController {

    private final GuardianService guardianService;
    private final CatalogLookupService catalogs;
    private final AcademicAccessService access;
    private final StudentRepository studentRepository;

    public GuardianController(
            GuardianService guardianService,
            CatalogLookupService catalogs,
            AcademicAccessService access,
            StudentRepository studentRepository) {
        this.guardianService = guardianService;
        this.catalogs = catalogs;
        this.access = access;
        this.studentRepository = studentRepository;
    }

    private GuardianDTO toDTO(Guardian guardian) {
        GuardianDTO dto = new GuardianDTO();
        dto.setId(guardian.getId());
        dto.setRut(guardian.getRut());
        dto.setFirstName(guardian.getFirstName());
        dto.setLastName(guardian.getLastName());
        dto.setSecondLastName(guardian.getSecondLastName());
        dto.setEmail(guardian.getEmail());
        dto.setPhone(guardian.getPhone());
        dto.setEmergencyPhone(guardian.getEmergencyPhone());
        dto.setAddress(guardian.getAddress());
        dto.setCommune(guardian.getCommune());
        dto.setCity(guardian.getCity());
        dto.setRelationship(catalogs.code("relationship_types", guardian.getRelationshipId()));
        dto.setOccupation(guardian.getOccupation());
        dto.setWorkplace(guardian.getWorkplace());
        dto.setWorkPhone(guardian.getWorkPhone());
        dto.setIsPrimary(guardian.getIsPrimary());
        dto.setUserId(guardian.getUserId());
        dto.setCreatedAt(guardian.getCreatedAt());
        dto.setUpdatedAt(guardian.getUpdatedAt());
        return dto;
    }

    private StudentDTO toStudentDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setRut(student.getRut());
        dto.setFirstName(student.getFirstName());
        dto.setSecondName(student.getSecondName());
        dto.setLastName(student.getLastName());
        dto.setMotherLastName(student.getMotherLastName());
        dto.setEmail(student.getEmail());
        dto.setEnrollmentNumber(student.getEnrollmentNumber());
        dto.setGuardianId(student.getGuardianId());
        dto.setStudentStatus(catalogs.code("student_statuses", student.getStudentStatusId()));
        return dto;
    }

    private Guardian toEntity(GuardianDTO dto) {
        Guardian guardian = new Guardian();
        guardian.setId(dto.getId());
        guardian.setRut(dto.getRut());
        guardian.setFirstName(dto.getFirstName());
        guardian.setLastName(dto.getLastName());
        guardian.setSecondLastName(dto.getSecondLastName());
        guardian.setEmail(dto.getEmail());
        guardian.setPhone(dto.getPhone());
        guardian.setEmergencyPhone(dto.getEmergencyPhone());
        guardian.setAddress(dto.getAddress());
        guardian.setCommune(dto.getCommune());
        guardian.setCity(dto.getCity());
        guardian.setRelationshipId(catalogs.requireId("relationship_types", dto.getRelationship()));
        guardian.setOccupation(dto.getOccupation());
        guardian.setWorkplace(dto.getWorkplace());
        guardian.setWorkPhone(dto.getWorkPhone());
        guardian.setIsPrimary(dto.getIsPrimary());
        guardian.setUserId(dto.getUserId());
        return guardian;
    }

    @GetMapping("/me")
    public ResponseEntity<GuardianDTO> getCurrentGuardian() {
        return access.currentGuardian()
                .map(guardian -> ResponseEntity.ok(toDTO(guardian)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me/students")
    public ResponseEntity<List<StudentDTO>> getMyStudents() {
        Long guardianId = access.requireGuardianId();
        List<StudentDTO> students = studentRepository.findByGuardianId(guardianId).stream()
                .map(this::toStudentDTO)
                .toList();
        return ResponseEntity.ok(students);
    }

    @PostMapping
    public ResponseEntity<GuardianDTO> createGuardian(@RequestBody GuardianDTO dto) {
        access.requireAdmin();
        return ResponseEntity.ok(toDTO(guardianService.createGuardian(toEntity(dto))));
    }

    @GetMapping
    public ResponseEntity<List<GuardianDTO>> getAllGuardians() {
        access.requireAdmin();
        return ResponseEntity.ok(guardianService.getAllGuardians().stream().map(this::toDTO).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuardianDTO> getGuardianById(@PathVariable Long id) {
        access.requireAdmin();
        return guardianService.getGuardianById(id)
                .map(g -> ResponseEntity.ok(toDTO(g)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuardianDTO> updateGuardian(@PathVariable Long id, @RequestBody GuardianDTO dto) {
        access.requireAdmin();
        Guardian updated = guardianService.updateGuardian(id, toEntity(dto));
        if (updated != null) {
            return ResponseEntity.ok(toDTO(updated));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuardian(@PathVariable Long id) {
        access.requireAdmin();
        guardianService.deleteGuardian(id);
        return ResponseEntity.noContent().build();
    }
}
