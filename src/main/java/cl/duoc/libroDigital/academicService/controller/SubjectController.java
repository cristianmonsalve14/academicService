package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.model.Subject;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
import cl.duoc.libroDigital.academicService.service.SubjectService;
import cl.duoc.libroDigital.academicService.dto.SubjectDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private CatalogLookupService catalogs;

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
        return toDTO(subjectService.createSubject(toEntity(dto)));
    }

    @GetMapping
    public List<SubjectDTO> getAllSubjects() {
        return subjectService.getAllSubjects().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public SubjectDTO getSubject(@PathVariable Long id) {
        return subjectService.getSubjectById(id).map(this::toDTO).orElse(null);
    }

    @PutMapping("/{id}")
    public SubjectDTO updateSubject(@PathVariable Long id, @RequestBody SubjectDTO dto) {
        return toDTO(subjectService.updateSubject(id, toEntity(dto)));
    }

    @DeleteMapping("/{id}")
    public void deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
    }
}
