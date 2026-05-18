package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.model.Enrollment;
import cl.duoc.libroDigital.academicService.dto.EnrollmentDTO;
import cl.duoc.libroDigital.academicService.service.EnrollmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    // ===== Mapper: Entity -> DTO =====
    private EnrollmentDTO toDTO(Enrollment e) {
        EnrollmentDTO dto = new EnrollmentDTO();

        dto.setId(e.getId());
        dto.setStudentId(e.getStudentId());
        dto.setCourseId(e.getCourseId());
        dto.setEnrollmentDate(e.getEnrollmentDate());
        dto.setAcademicYear(e.getAcademicYear());
        dto.setEnrollmentStatus(e.getEnrollmentStatus());
        dto.setIsRegular(e.getIsRegular());
        dto.setObservations(e.getObservations());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());

        return dto;
    }

    // ===== Mapper: DTO -> Entity =====
    private Enrollment toEntity(EnrollmentDTO dto) {
        Enrollment e = new Enrollment();

        e.setId(dto.getId());
        e.setStudentId(dto.getStudentId());
        e.setCourseId(dto.getCourseId());
        e.setEnrollmentDate(dto.getEnrollmentDate());
        e.setAcademicYear(dto.getAcademicYear());
        e.setEnrollmentStatus(dto.getEnrollmentStatus());
        e.setIsRegular(dto.getIsRegular());
        e.setObservations(dto.getObservations());

        return e;
    }

    // ===== Crear =====
    @PostMapping
    public EnrollmentDTO createEnrollment(@RequestBody EnrollmentDTO dto) {
        Enrollment created = enrollmentService.createEnrollment(toEntity(dto));
        return toDTO(created);
    }

    // ===== Listar =====
    @GetMapping
    public List<EnrollmentDTO> getAllEnrollments() {
        return enrollmentService.getAllEnrollments()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ===== Obtener por ID =====
    @GetMapping("/{id}")
    public EnrollmentDTO getEnrollment(@PathVariable Long id) {
        return enrollmentService.getEnrollmentById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    // ===== Actualizar =====
    @PutMapping("/{id}")
    public EnrollmentDTO updateEnrollment(@PathVariable Long id, @RequestBody EnrollmentDTO dto) {
        Enrollment updated = enrollmentService.updateEnrollment(id, toEntity(dto));
        return toDTO(updated);
    }

    // ===== Eliminar =====
    @DeleteMapping("/{id}")
    public void deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
    }
}