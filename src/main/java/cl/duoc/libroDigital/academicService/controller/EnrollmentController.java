package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.model.Enrollment;
import cl.duoc.libroDigital.academicService.dto.EnrollmentDTO;
import cl.duoc.libroDigital.academicService.repository.StudentRepository;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
import cl.duoc.libroDigital.academicService.service.EnrollmentService;
import cl.duoc.libroDigital.academicService.security.AcademicAccessService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final CatalogLookupService catalogs;
    private final AcademicAccessService access;
    private final StudentRepository studentRepository;

    public EnrollmentController(
            EnrollmentService enrollmentService,
            CatalogLookupService catalogs,
            AcademicAccessService access,
            StudentRepository studentRepository) {
        this.enrollmentService = enrollmentService;
        this.catalogs = catalogs;
        this.access = access;
        this.studentRepository = studentRepository;
    }

    private EnrollmentDTO toDTO(Enrollment e) {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(e.getId());
        dto.setStudentId(e.getStudentId());
        dto.setCourseId(e.getCourseId());
        if (e.getStudentId() != null) {
            studentRepository.findById(e.getStudentId())
                    .ifPresent(student -> dto.setEnrollmentNumber(student.getEnrollmentNumber()));
        }
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
        access.requireAdmin();
        return toDTO(enrollmentService.createEnrollment(toEntity(dto)));
    }

    @GetMapping("/student/{studentId}")
    public List<EnrollmentDTO> getEnrollmentsByStudent(@PathVariable Long studentId) {
        access.ensureCanReadStudent(studentId);
        return enrollmentService.getEnrollmentsByStudent(studentId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping
    public List<EnrollmentDTO> getAllEnrollments() {
        if (access.isAdmin()) {
            return enrollmentService.getAllEnrollments().stream().map(this::toDTO).collect(Collectors.toList());
        }
        if (access.isStudent()) {
            Long studentId = access.requireStudentId();
            return enrollmentService.getEnrollmentsByStudent(studentId).stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }
        if (access.isGuardian()) {
            Long guardianId = access.requireGuardianId();
            return enrollmentService.getAllEnrollments().stream()
                    .filter(enrollment -> access.guardianStudentIds(guardianId).contains(enrollment.getStudentId()))
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }
        Long teacherId = access.requireTeacherId();
        Set<Long> courseIds = access.teacherCourseIds(teacherId);
        return enrollmentService.getAllEnrollments().stream()
                .filter(enrollment -> courseIds.contains(enrollment.getCourseId()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EnrollmentDTO getEnrollment(@PathVariable Long id) {
        Enrollment enrollment = enrollmentService.getEnrollmentById(id)
                .orElse(null);
        if (enrollment == null) {
            return null;
        }
        access.ensureCanReadStudent(enrollment.getStudentId());
        return toDTO(enrollment);
    }

    @PutMapping("/{id}")
    public EnrollmentDTO updateEnrollment(@PathVariable Long id, @RequestBody EnrollmentDTO dto) {
        access.requireAdmin();
        return toDTO(enrollmentService.updateEnrollment(id, toEntity(dto)));
    }

    @DeleteMapping("/{id}")
    public void deleteEnrollment(@PathVariable Long id) {
        access.requireAdmin();
        enrollmentService.deleteEnrollment(id);
    }
}
