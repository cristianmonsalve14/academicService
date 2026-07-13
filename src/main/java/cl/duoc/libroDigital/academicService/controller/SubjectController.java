package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.model.Subject;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
import cl.duoc.libroDigital.academicService.service.SubjectService;
import cl.duoc.libroDigital.academicService.dto.SubjectDTO;
import cl.duoc.libroDigital.academicService.security.AcademicAccessService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectService subjectService;
    private final CatalogLookupService catalogs;
    private final AcademicAccessService access;

    public SubjectController(
            SubjectService subjectService,
            CatalogLookupService catalogs,
            AcademicAccessService access) {
        this.subjectService = subjectService;
        this.catalogs = catalogs;
        this.access = access;
    }

    private SubjectDTO toDTO(Subject subject) {
        SubjectDTO dto = new SubjectDTO();
        dto.setId(subject.getId());
        dto.setSubjectName(subject.getSubjectName());
        dto.setSubjectCode(subject.getSubjectCode());
        dto.setDescription(subject.getDescription());
        dto.setSubjectType(catalogs.code("subject_types", subject.getSubjectTypeId()));
        dto.setWeeklyHours(subject.getWeeklyHours());
        dto.setTeacherId(subject.getTeacherId());
        dto.setCourseId(subject.getCourseId());
        dto.setCreatedAt(subject.getCreatedAt());
        dto.setUpdatedAt(subject.getUpdatedAt());
        return dto;
    }

    private Subject toEntity(SubjectDTO dto) {
        Subject subject = new Subject();
        subject.setId(dto.getId());
        subject.setSubjectName(dto.getSubjectName());
        subject.setSubjectCode(dto.getSubjectCode());
        subject.setDescription(dto.getDescription());
        if (dto.getSubjectType() != null && !dto.getSubjectType().isBlank()) {
            subject.setSubjectTypeId(catalogs.requireId("subject_types", dto.getSubjectType()));
        }
        subject.setWeeklyHours(dto.getWeeklyHours());
        subject.setTeacherId(dto.getTeacherId());
        subject.setCourseId(dto.getCourseId());
        subject.setCreatedAt(dto.getCreatedAt());
        subject.setUpdatedAt(dto.getUpdatedAt());
        return subject;
    }

    @PostMapping
    public SubjectDTO createSubject(@RequestBody SubjectDTO dto) {
        access.requireSuperAdmin();
        return toDTO(subjectService.createSubject(toEntity(dto)));
    }

    @GetMapping
    public List<SubjectDTO> getAllSubjects() {
        if (access.isAdmin()) {
            return subjectService.getAllSubjects().stream().map(this::toDTO).collect(Collectors.toList());
        }
        Long teacherId = access.requireTeacherId();
        return subjectService.getSubjectsByTeacher(teacherId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public SubjectDTO getSubject(@PathVariable Long id) {
        access.ensureCanReadSubject(id);
        return subjectService.getSubjectById(id).map(this::toDTO).orElse(null);
    }

    @PutMapping("/{id}")
    public SubjectDTO updateSubject(@PathVariable Long id, @RequestBody SubjectDTO dto) {
        access.requireAdmin();
        return toDTO(subjectService.updateSubject(id, toEntity(dto)));
    }

    @DeleteMapping("/{id}")
    public void deleteSubject(@PathVariable Long id) {
        access.requireSuperAdmin();
        subjectService.deleteSubject(id);
    }
}
