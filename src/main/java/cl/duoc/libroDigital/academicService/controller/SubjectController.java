package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.model.Subject;
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

    // ===== Mapper: Entity -> DTO =====
    private SubjectDTO toDTO(Subject subject) {
        SubjectDTO dto = new SubjectDTO();

        dto.setId(subject.getId());
        dto.setSubjectName(subject.getSubjectName());
        dto.setSubjectCode(subject.getSubjectCode());
        dto.setDescription(subject.getDescription());
        dto.setSubjectType(subject.getSubjectType());

        dto.setWeeklyHours(subject.getWeeklyHours());

        dto.setTeacherId(subject.getTeacherId());
        dto.setCourseId(subject.getCourseId());

        dto.setCreatedAt(subject.getCreatedAt());
        dto.setUpdatedAt(subject.getUpdatedAt());

        return dto;
    }

    // ===== Mapper: DTO -> Entity =====
    private Subject toEntity(SubjectDTO dto) {
        Subject subject = new Subject();

        subject.setId(dto.getId());
        subject.setSubjectName(dto.getSubjectName());
        subject.setSubjectCode(dto.getSubjectCode());
        subject.setDescription(dto.getDescription());
        subject.setSubjectType(dto.getSubjectType());

        subject.setWeeklyHours(dto.getWeeklyHours());

        subject.setTeacherId(dto.getTeacherId());
        subject.setCourseId(dto.getCourseId());

        subject.setCreatedAt(dto.getCreatedAt());
        subject.setUpdatedAt(dto.getUpdatedAt());

        return subject;
    }

    // ===== Crear =====
    @PostMapping
    public SubjectDTO createSubject(@RequestBody SubjectDTO dto) {
        Subject created = subjectService.createSubject(toEntity(dto));
        return toDTO(created);
    }

    // ===== Listar =====
    @GetMapping
    public List<SubjectDTO> getAllSubjects() {
        return subjectService.getAllSubjects()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ===== Obtener por ID =====
    @GetMapping("/{id}")
    public SubjectDTO getSubject(@PathVariable Long id) {
        return subjectService.getSubjectById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    // ===== Actualizar =====
    @PutMapping("/{id}")
    public SubjectDTO updateSubject(@PathVariable Long id, @RequestBody SubjectDTO dto) {
        Subject updated = subjectService.updateSubject(id, toEntity(dto));
        return toDTO(updated);
    }

    // ===== Eliminar =====
    @DeleteMapping("/{id}")
    public void deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
    }
}
