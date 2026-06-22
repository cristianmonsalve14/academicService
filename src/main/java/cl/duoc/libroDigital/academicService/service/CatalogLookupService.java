package cl.duoc.libroDigital.academicService.service;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CatalogLookupService {

    @PersistenceContext
    private EntityManager em;

    private final Map<String, Map<String, Short>> codeToId = new HashMap<>();
    private final Map<String, Map<Short, String>> idToCode = new HashMap<>();

    @PostConstruct
    public void loadCatalogs() {
        load("student_statuses", "StudentStatus");
        load("teacher_statuses", "TeacherStatus");
        load("course_statuses", "CourseStatus");
        load("enrollment_statuses", "EnrollmentStatus");
        load("evaluation_types", "EvaluationType");
        load("evaluation_statuses", "EvaluationStatus");
        load("grade_statuses", "GradeStatus");
        load("relationship_types", "RelationshipType");
        load("contract_types", "ContractType");
        load("subject_types", "SubjectType");
        load("shifts", "Shift");
        load("education_levels", "EducationLevel");
        load("academic_years", "AcademicYear");
    }

    private void load(String table, String entity) {
        List<Object[]> rows = em.createQuery(
                "SELECT c.id, c.code FROM cl.duoc.libroDigital.academicService.model.catalog." + entity + " c",
                Object[].class).getResultList();
        Map<String, Short> codes = new HashMap<>();
        Map<Short, String> ids = new HashMap<>();
        for (Object[] row : rows) {
            Short id = (Short) row[0];
            String code = ((String) row[1]).toUpperCase();
            codes.put(code, id);
            ids.put(id, code);
        }
        codeToId.put(table, codes);
        idToCode.put(table, ids);
    }

    public Short requireId(String table, String code) {
        if (code == null || code.isBlank()) {
            return defaultId(table);
        }
        String normalized = code.trim().toUpperCase().replace('Ñ', 'N');
        if ("shifts".equals(table)) {
            Short shiftId = resolveShiftId(normalized);
            if (shiftId != null) {
                return shiftId;
            }
        }
        return codeToId.getOrDefault(table, Map.of())
                .getOrDefault(normalized, defaultId(table));
    }

    public String code(String table, Short id) {
        if (id == null) {
            return null;
        }
        String stored = idToCode.getOrDefault(table, Map.of()).get(id);
        if ("shifts".equals(table)) {
            return normalizeShiftCode(stored);
        }
        return stored;
    }

    private Short resolveShiftId(String normalized) {
        Map<String, Short> shifts = codeToId.getOrDefault("shifts", Map.of());
        Short id = shifts.get(normalized);
        if (id != null) {
            return id;
        }
        if ("COMPLETA".equals(normalized) || "JORNADA_COMPLETA".equals(normalized)) {
            return shifts.get("VESPERTINO");
        }
        return null;
    }

    private String normalizeShiftCode(String code) {
        if (code == null) {
            return null;
        }
        if ("VESPERTINO".equals(code)) {
            return "COMPLETA";
        }
        return code;
    }

    public String label(String table, Short id) {
        if (id == null) {
            return null;
        }
        String entity = entityFor(table);
        return em.createQuery(
                        "SELECT c.label FROM cl.duoc.libroDigital.academicService.model.catalog." + entity + " c WHERE c.id = :id",
                        String.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public String educationStage(Short levelId) {
        if (levelId == null) {
            return null;
        }
        return em.createQuery(
                        "SELECT c.stage FROM cl.duoc.libroDigital.academicService.model.catalog.EducationLevel c WHERE c.id = :id",
                        String.class)
                .setParameter("id", levelId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public Integer academicYearValue(Short academicYearId) {
        if (academicYearId == null) {
            return null;
        }
        return em.createQuery(
                        "SELECT c.year FROM cl.duoc.libroDigital.academicService.model.catalog.AcademicYear c WHERE c.id = :id",
                        Integer.class)
                .setParameter("id", academicYearId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public java.time.LocalDate academicYearStartDate(Short academicYearId) {
        if (academicYearId == null) {
            return null;
        }
        return em.createQuery(
                        "SELECT c.startDate FROM cl.duoc.libroDigital.academicService.model.catalog.AcademicYear c WHERE c.id = :id",
                        java.time.LocalDate.class)
                .setParameter("id", academicYearId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public Short academicYearIdFromDate(java.time.LocalDate date) {
        if (date == null) {
            return defaultId("academic_years");
        }
        return requireId("academic_years", String.valueOf(date.getYear()));
    }

    public Short academicYearIdFromYear(Integer year) {
        if (year == null) {
            return defaultId("academic_years");
        }
        return requireId("academic_years", String.valueOf(year));
    }

    public Short educationLevelIdFromLabel(String label) {
        if (label == null || label.isBlank()) {
            return defaultId("education_levels");
        }
        return codeToId.get("education_levels").entrySet().stream()
                .filter(e -> label.equalsIgnoreCase(this.label("education_levels", e.getValue())))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(defaultId("education_levels"));
    }

    private Short defaultId(String table) {
        return switch (table) {
            case "teacher_statuses", "course_statuses", "enrollment_statuses",
                 "evaluation_types", "evaluation_statuses", "grade_statuses",
                 "relationship_types", "student_statuses" -> (short) 1;
            case "academic_years" -> (short) 2;
            case "education_levels" -> (short) 9;
            default -> null;
        };
    }

    private String entityFor(String table) {
        return switch (table) {
            case "student_statuses" -> "StudentStatus";
            case "teacher_statuses" -> "TeacherStatus";
            case "course_statuses" -> "CourseStatus";
            case "enrollment_statuses" -> "EnrollmentStatus";
            case "evaluation_types" -> "EvaluationType";
            case "evaluation_statuses" -> "EvaluationStatus";
            case "grade_statuses" -> "GradeStatus";
            case "relationship_types" -> "RelationshipType";
            case "contract_types" -> "ContractType";
            case "subject_types" -> "SubjectType";
            case "shifts" -> "Shift";
            case "education_levels" -> "EducationLevel";
            case "academic_years" -> "AcademicYear";
            default -> throw new IllegalArgumentException("Catálogo desconocido: " + table);
        };
    }
}
