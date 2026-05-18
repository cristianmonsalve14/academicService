package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.model.Evaluation;
import cl.duoc.libroDigital.academicService.dto.EvaluationDTO;
import cl.duoc.libroDigital.academicService.service.EvaluationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/evaluations")
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    // ===== Mapper: Entity -> DTO =====
    private EvaluationDTO toDTO(Evaluation e) {
        EvaluationDTO dto = new EvaluationDTO();

        dto.setId(e.getId());
        dto.setSubjectId(e.getSubjectId());
        dto.setCourseId(e.getCourseId());
        dto.setName(e.getName());
        dto.setDate(e.getDate());
        dto.setEvaluationType(e.getEvaluationType());
        dto.setEvaluationStatus(e.getEvaluationStatus());
        dto.setMaxScore(e.getMaxScore());
        dto.setWeight(e.getWeight());
        dto.setDescription(e.getDescription());
        dto.setGrade(e.getGrade());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());

        return dto;
    }

    // ===== Mapper: DTO -> Entity =====
    private Evaluation toEntity(EvaluationDTO dto) {
        Evaluation e = new Evaluation();

        e.setId(dto.getId());
        e.setSubjectId(dto.getSubjectId());
        e.setCourseId(dto.getCourseId());
        e.setName(dto.getName());
        e.setDate(dto.getDate());
        e.setEvaluationType(dto.getEvaluationType());
        e.setEvaluationStatus(dto.getEvaluationStatus());
        e.setMaxScore(dto.getMaxScore());
        e.setWeight(dto.getWeight());
        e.setDescription(dto.getDescription());
        e.setGrade(dto.getGrade());

        return e;
    }

    // ===== Crear =====
    @PostMapping
    public EvaluationDTO createEvaluation(@RequestBody EvaluationDTO dto) {
        Evaluation created = evaluationService.createEvaluation(toEntity(dto));
        return toDTO(created);
    }

    // ===== Listar =====
    @GetMapping
    public List<EvaluationDTO> getAllEvaluations() {
        return evaluationService.getAllEvaluations()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ===== Obtener por ID =====
    @GetMapping("/{id}")
    public EvaluationDTO getEvaluation(@PathVariable Long id) {
        return evaluationService.getEvaluationById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    // ===== Actualizar =====
    @PutMapping("/{id}")
    public EvaluationDTO updateEvaluation(@PathVariable Long id, @RequestBody EvaluationDTO dto) {
        Evaluation updated = evaluationService.updateEvaluation(id, toEntity(dto));
        return toDTO(updated);
    }

    // ===== Eliminar =====
    @DeleteMapping("/{id}")
    public void deleteEvaluation(@PathVariable Long id) {
        evaluationService.deleteEvaluation(id);
    }
}
