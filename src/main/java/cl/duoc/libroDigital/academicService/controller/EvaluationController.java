package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.model.Evaluation;
import cl.duoc.libroDigital.academicService.dto.EvaluationDTO;
import cl.duoc.libroDigital.academicService.repository.SubjectRepository;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
import cl.duoc.libroDigital.academicService.service.EvaluationService;
import cl.duoc.libroDigital.academicService.service.MessageService;
import cl.duoc.libroDigital.academicService.security.AcademicAccessService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/evaluations")
public class EvaluationController {

    private static final Logger log = LoggerFactory.getLogger(EvaluationController.class);

    private final EvaluationService evaluationService;
    private final SubjectRepository subjectRepository;
    private final CatalogLookupService catalogs;
    private final AcademicAccessService access;
    private final MessageService messageService;

    public EvaluationController(
            EvaluationService evaluationService,
            SubjectRepository subjectRepository,
            CatalogLookupService catalogs,
            AcademicAccessService access,
            MessageService messageService) {
        this.evaluationService = evaluationService;
        this.subjectRepository = subjectRepository;
        this.catalogs = catalogs;
        this.access = access;
        this.messageService = messageService;
    }

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
        access.ensureCanManageEvaluationSubject(dto.getSubjectId());
        Evaluation created = evaluationService.createEvaluation(toEntity(dto));
        try {
            String typeLabel = catalogs.code("evaluation_types", created.getEvaluationTypeId());
            messageService.notifyGuardiansEvaluationCreated(created, typeLabel);
        } catch (Exception ex) {
            log.warn("Evaluación {} creada, pero falló el aviso a apoderados: {}",
                    created.getId(), ex.getMessage());
        }
        return toDTO(created);
    }

    @GetMapping
    public List<EvaluationDTO> getAllEvaluations() {
        if (access.isAdmin()) {
            return evaluationService.getAllEvaluations().stream().map(this::toDTO).collect(Collectors.toList());
        }
        Long teacherId = access.requireTeacherId();
        List<Long> subjectIds = access.teacherSubjectIds(teacherId);
        return evaluationService.getAllEvaluations().stream()
                .filter(e -> e.getSubjectId() != null && subjectIds.contains(e.getSubjectId()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EvaluationDTO getEvaluation(@PathVariable Long id) {
        Evaluation evaluation = evaluationService.getEvaluationById(id).orElse(null);
        if (evaluation == null) {
            return null;
        }
        access.ensureCanReadSubject(evaluation.getSubjectId());
        return toDTO(evaluation);
    }

    @PutMapping("/{id}")
    public EvaluationDTO updateEvaluation(@PathVariable Long id, @RequestBody EvaluationDTO dto) {
        access.ensureCanManageEvaluation(id);
        Evaluation updated = evaluationService.updateEvaluation(id, toEntity(dto));
        return toDTO(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteEvaluation(@PathVariable Long id) {
        access.ensureCanManageEvaluation(id);
        evaluationService.deleteEvaluation(id);
    }
}
