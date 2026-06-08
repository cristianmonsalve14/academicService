package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.model.Enrollment;
import cl.duoc.libroDigital.academicService.dto.EnrollmentDTO;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
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

    @Autowired
    private CatalogLookupService catalogs;

    private EnrollmentDTO toDTO(Enrollment e) {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(e.getId());
        dto.setStudentId(e.getStudentId());
        dto.setCourseId(e.getCourseId());
        dto.setEnrollmentDate(e.getEnrollmentDate());
        dto.setAcademicYear(catalogs.academicYearValue(e.getAcademicYearId()));
        dto.setEnrollmentStatus(catalogs.code("enrollment_statuses", e.getEnrollmentStatusId()));
        dto.setIsRegular(e.getIsRegular());
        dto.setObservations(e.getObservations());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }

    private Enrollment toEntity(EnrollmentDTO dto) {
        Enrollment e = new Enrollment();
        e.setId(dto.getId());
        e.setStudentId(dto.getStudentId());
        e.setCourseId(dto.getCourseId());
        e.setEnrollmentDate(dto.getEnrollmentDate());
        e.setAcademicYearId(catalogs.academicYearIdFromYear(dto.getAcademicYear()));
        e.setEnrollmentStatusId(catalogs.requireId("enrollment_statuses", dto.getEnrollmentStatus()));
        e.setIsRegular(dto.getIsRegular());
        e.setObservations(dto.getObservations());
        return e;
    }

    @PostMapping
    public EnrollmentDTO createEnrollment(@RequestBody EnrollmentDTO dto) {
        return toDTO(enrollmentService.createEnrollment(toEntity(dto)));
    }

    @GetMapping
    public List<EnrollmentDTO> getAllEnrollments() {
        return enrollmentService.getAllEnrollments().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EnrollmentDTO getEnrollment(@PathVariable Long id) {
        return enrollmentService.getEnrollmentById(id).map(this::toDTO).orElse(null);
    }

    @PutMapping("/{id}")
    public EnrollmentDTO updateEnrollment(@PathVariable Long id, @RequestBody EnrollmentDTO dto) {
        return toDTO(enrollmentService.updateEnrollment(id, toEntity(dto)));
    }

    @DeleteMapping("/{id}")
    public void deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
    }
}
