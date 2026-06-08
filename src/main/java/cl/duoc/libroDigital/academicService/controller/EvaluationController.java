package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.model.Evaluation;
import cl.duoc.libroDigital.academicService.dto.EvaluationDTO;
import cl.duoc.libroDigital.academicService.repository.SubjectRepository;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
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

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private CatalogLookupService catalogs;

    private EvaluationDTO toDTO(Evaluation e) {
        EvaluationDTO dto = new EvaluationDTO();
        dto.setId(e.getId());
        dto.setSubjectId(e.getSubjectId());
        if (e.getSubjectId() != null) {
            subjectRepository.findById(e.getSubjectId())
                    .ifPresent(s -> dto.setCourseId(s.getCourseId()));
        }
        dto.setName(e.getName());
        dto.setDate(e.getDate());
        dto.setEvaluationType(catalogs.code("evaluation_types", e.getEvaluationTypeId()));
        dto.setEvaluationStatus(catalogs.code("evaluation_statuses", e.getEvaluationStatusId()));
        dto.setMaxScore(e.getMaxScore());
        dto.setWeight(e.getWeight());
        dto.setDescription(e.getDescription());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }

    private Evaluation toEntity(EvaluationDTO dto) {
        Evaluation e = new Evaluation();
        e.setId(dto.getId());
        e.setSubjectId(dto.getSubjectId());
        e.setName(dto.getName());
        e.setDate(dto.getDate());
        e.setEvaluationTypeId(catalogs.requireId("evaluation_types", dto.getEvaluationType()));
        e.setEvaluationStatusId(catalogs.requireId("evaluation_statuses", dto.getEvaluationStatus()));
        e.setMaxScore(dto.getMaxScore());
        e.setWeight(dto.getWeight());
        e.setDescription(dto.getDescription());
        return e;
    }

    @PostMapping
    public EvaluationDTO createEvaluation(@RequestBody EvaluationDTO dto) {
        Evaluation created = evaluationService.createEvaluation(toEntity(dto));
        return toDTO(created);
    }

    @GetMapping
    public List<EvaluationDTO> getAllEvaluations() {
        return evaluationService.getAllEvaluations().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EvaluationDTO getEvaluation(@PathVariable Long id) {
        return evaluationService.getEvaluationById(id).map(this::toDTO).orElse(null);
    }

    @PutMapping("/{id}")
    public EvaluationDTO updateEvaluation(@PathVariable Long id, @RequestBody EvaluationDTO dto) {
        Evaluation updated = evaluationService.updateEvaluation(id, toEntity(dto));
        return toDTO(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteEvaluation(@PathVariable Long id) {
        evaluationService.deleteEvaluation(id);
    }
}
