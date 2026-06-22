package cl.duoc.libroDigital.academicService.validation;

import cl.duoc.libroDigital.academicService.exception.BadRequestException;
import cl.duoc.libroDigital.academicService.exception.ConflictException;
import cl.duoc.libroDigital.academicService.exception.NotFoundException;
import cl.duoc.libroDigital.academicService.model.Course;
import cl.duoc.libroDigital.academicService.model.Enrollment;
import cl.duoc.libroDigital.academicService.model.Evaluation;
import cl.duoc.libroDigital.academicService.model.Grade;
import cl.duoc.libroDigital.academicService.model.Guardian;
import cl.duoc.libroDigital.academicService.model.Student;
import cl.duoc.libroDigital.academicService.model.Subject;
import cl.duoc.libroDigital.academicService.model.Teacher;
import cl.duoc.libroDigital.academicService.repository.CourseRepository;
import cl.duoc.libroDigital.academicService.repository.EnrollmentRepository;
import cl.duoc.libroDigital.academicService.repository.EvaluationRepository;
import cl.duoc.libroDigital.academicService.repository.GradeRepository;
import cl.duoc.libroDigital.academicService.repository.GuardianRepository;
import cl.duoc.libroDigital.academicService.repository.StudentRepository;
import cl.duoc.libroDigital.academicService.repository.SubjectRepository;
import cl.duoc.libroDigital.academicService.repository.TeacherRepository;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
import cl.duoc.libroDigital.academicService.util.EmailUtil;
import cl.duoc.libroDigital.academicService.util.PhoneUtil;
import cl.duoc.libroDigital.academicService.util.RutUtil;
import cl.duoc.libroDigital.academicService.util.TextUtil;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class AcademicEntityValidator {

    private final StudentRepository studentRepository;
    private final GuardianRepository guardianRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EvaluationRepository evaluationRepository;
    private final GradeRepository gradeRepository;
    private final CatalogLookupService catalogs;

    public AcademicEntityValidator(
            StudentRepository studentRepository,
            GuardianRepository guardianRepository,
            TeacherRepository teacherRepository,
            CourseRepository courseRepository,
            SubjectRepository subjectRepository,
            EnrollmentRepository enrollmentRepository,
            EvaluationRepository evaluationRepository,
            GradeRepository gradeRepository,
            CatalogLookupService catalogs) {
        this.studentRepository = studentRepository;
        this.guardianRepository = guardianRepository;
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
        this.subjectRepository = subjectRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.evaluationRepository = evaluationRepository;
        this.gradeRepository = gradeRepository;
        this.catalogs = catalogs;
    }

    public void validateStudentForSave(Student student, Long excludeId) {
        student.setRut(RutUtil.normalizeAndValidate(student.getRut()));
        student.setFirstName(TextUtil.requireNonBlank(student.getFirstName(), "El nombre"));
        student.setLastName(TextUtil.requireNonBlank(student.getLastName(), "El apellido paterno"));
        student.setMotherLastName(TextUtil.requireNonBlank(student.getMotherLastName(), "El apellido materno"));
        student.setEmail(EmailUtil.normalizeAndValidate(student.getEmail()));
        student.setPhone(PhoneUtil.normalizeAndValidateOptional(student.getPhone()));

        if (student.getDateOfBirth() != null) {
            validatePastDate(student.getDateOfBirth(), "La fecha de nacimiento");
            if (student.getDateOfBirth().isBefore(LocalDate.now().minusYears(30))) {
                throw new BadRequestException("La fecha de nacimiento no parece válida para un estudiante");
            }
        }

        if (student.getGuardianId() != null
                && !guardianRepository.existsById(student.getGuardianId())) {
            throw new BadRequestException("El apoderado seleccionado no existe");
        }

        ensureUniqueRut(studentRepository.findByRut(student.getRut()), excludeId, "Ya existe un estudiante con ese RUT");
        ensureUniqueEmail(studentRepository.findByEmail(student.getEmail()), excludeId, "Ya existe un estudiante con ese email");
    }

    public void validateGuardianForSave(Guardian guardian, Long excludeId) {
        guardian.setRut(RutUtil.normalizeAndValidate(guardian.getRut()));
        guardian.setFirstName(TextUtil.requireNonBlank(guardian.getFirstName(), "El nombre"));
        guardian.setLastName(TextUtil.requireNonBlank(guardian.getLastName(), "El apellido paterno"));
        guardian.setEmail(EmailUtil.normalizeAndValidate(guardian.getEmail()));
        guardian.setPhone(PhoneUtil.normalizeAndValidateRequired(guardian.getPhone()));
        guardian.setEmergencyPhone(PhoneUtil.normalizeAndValidateOptional(guardian.getEmergencyPhone()));

        ensureUniqueRut(guardianRepository.findByRut(guardian.getRut()), excludeId, "Ya existe un apoderado con ese RUT");
        ensureUniqueEmail(guardianRepository.findByEmail(guardian.getEmail()), excludeId, "Ya existe un apoderado con ese email");
    }

    public void validateTeacherForSave(Teacher teacher, Long excludeId) {
        teacher.setRut(RutUtil.normalizeAndValidate(teacher.getRut()));
        teacher.setFirstName(TextUtil.requireNonBlank(teacher.getFirstName(), "El nombre"));
        teacher.setLastName(TextUtil.requireNonBlank(teacher.getLastName(), "El apellido paterno"));
        teacher.setEmail(EmailUtil.normalizeAndValidate(teacher.getEmail()));
        teacher.setPhone(PhoneUtil.normalizeAndValidateOptional(teacher.getPhone()));

        ensureUniqueRut(teacherRepository.findByRut(teacher.getRut()), excludeId, "Ya existe un profesor con ese RUT");
        ensureUniqueEmail(teacherRepository.findByEmail(teacher.getEmail()), excludeId, "Ya existe un profesor con ese email");
    }

    public void validateCourseForSave(Course course) {
        TextUtil.requireNonBlank(course.getName(), "El grado del curso");
        if (course.getLevelId() == null && (course.getName() == null || course.getName().isBlank())) {
            TextUtil.requireNonBlank(course.getName(), "El nombre del curso");
        }
        if (course.getAcademicYearId() == null) {
            throw new BadRequestException("El año académico es obligatorio");
        }
        if (course.getMaxCapacity() != null && course.getMaxCapacity() < 1) {
            throw new BadRequestException("La capacidad máxima debe ser al menos 1");
        }
        if (course.getHeadTeacherId() != null
                && !teacherRepository.existsById(course.getHeadTeacherId())) {
            throw new BadRequestException("El profesor jefe seleccionado no existe");
        }
    }

    public void validateEnrollmentForSave(Enrollment enrollment, Long excludeId) {
        if (enrollment.getStudentId() == null) {
            throw new BadRequestException("El estudiante es obligatorio");
        }
        if (enrollment.getCourseId() == null) {
            throw new BadRequestException("El curso es obligatorio");
        }
        if (!studentRepository.existsById(enrollment.getStudentId())) {
            throw new BadRequestException("El estudiante seleccionado no existe");
        }
        if (!courseRepository.existsById(enrollment.getCourseId())) {
            throw new BadRequestException("El curso seleccionado no existe");
        }
        if (enrollment.getEnrollmentStatusId() == null) {
            enrollment.setEnrollmentStatusId(catalogs.requireId("enrollment_statuses", "ACTIVO"));
        }

        Short activeStatusId = catalogs.requireId("enrollment_statuses", "ACTIVO");
        if (activeStatusId.equals(enrollment.getEnrollmentStatusId())) {
            boolean duplicate = enrollmentRepository
                    .findByStudentIdAndCourseIdAndEnrollmentStatusId(
                            enrollment.getStudentId(),
                            enrollment.getCourseId(),
                            activeStatusId)
                    .stream()
                    .anyMatch(existing -> excludeId == null || !existing.getId().equals(excludeId));
            if (duplicate) {
                throw new ConflictException("El estudiante ya tiene una matrícula activa en ese curso");
            }
        }

        if (enrollment.getEnrollmentDate() != null) {
            validateNotFutureDate(enrollment.getEnrollmentDate(), "La fecha de matrícula");
        }
    }

    public void validateSubjectForSave(Subject subject, Long excludeId) {
        subject.setSubjectCode(TextUtil.requireNonBlank(subject.getSubjectCode(), "El código de la asignatura"));
        subject.setSubjectName(TextUtil.requireNonBlank(subject.getSubjectName(), "El nombre de la asignatura"));
        if ("TEMP".equalsIgnoreCase(subject.getSubjectName())) {
            throw new BadRequestException("El nombre de la asignatura no puede ser TEMP");
        }
        if (subject.getCourseId() == null) {
            throw new BadRequestException("El curso es obligatorio para la asignatura");
        }
        if (!courseRepository.existsById(subject.getCourseId())) {
            throw new BadRequestException("El curso seleccionado no existe");
        }
        if (subject.getTeacherId() != null && !teacherRepository.existsById(subject.getTeacherId())) {
            throw new BadRequestException("El profesor seleccionado no existe");
        }
        if (subject.getWeeklyHours() != null) {
            int hours = subject.getWeeklyHours();
            if (hours < 1 || hours > 12) {
                throw new BadRequestException("Las horas semanales deben estar entre 1 y 12");
            }
        }

        subjectRepository.findBySubjectCode(subject.getSubjectCode()).ifPresent(existing -> {
            if (excludeId == null || !existing.getId().equals(excludeId)) {
                throw new ConflictException("Ya existe una asignatura con ese código");
            }
        });
    }

    public void validateEvaluationForSave(Evaluation evaluation, Long excludeId) {
        evaluation.setName(TextUtil.requireNonBlank(evaluation.getName(), "El nombre de la evaluación"));
        if (evaluation.getSubjectId() == null) {
            throw new BadRequestException("La asignatura es obligatoria");
        }
        if (evaluation.getDate() == null) {
            throw new BadRequestException("La fecha de la evaluación es obligatoria");
        }
        if (evaluation.getEvaluationTypeId() == null) {
            throw new BadRequestException("El tipo de evaluación es obligatorio");
        }

        subjectRepository.findById(evaluation.getSubjectId())
                .orElseThrow(() -> new BadRequestException("La asignatura seleccionada no existe"));

        if (evaluation.getWeight() != null) {
            double weight = evaluation.getWeight().doubleValue();
            if (weight < 0 || weight > 100) {
                throw new BadRequestException("La ponderación debe estar entre 0 y 100");
            }
        }
        if (evaluation.getMaxScore() != null && evaluation.getMaxScore().doubleValue() <= 0) {
            throw new BadRequestException("La nota máxima debe ser mayor a 0");
        }
    }

    public void validateGradeForSave(Grade grade, Long excludeId) {
        if (grade.getStudentId() == null) {
            throw new BadRequestException("El estudiante es obligatorio");
        }
        if (grade.getEvaluationId() == null) {
            throw new BadRequestException("La evaluación es obligatoria");
        }
        if (!studentRepository.existsById(grade.getStudentId())) {
            throw new BadRequestException("El estudiante seleccionado no existe");
        }

        Evaluation evaluation = evaluationRepository.findById(grade.getEvaluationId())
                .orElseThrow(() -> new BadRequestException("La evaluación seleccionada no existe"));

        Subject subject = subjectRepository.findById(evaluation.getSubjectId())
                .orElseThrow(() -> new BadRequestException("La asignatura de la evaluación no existe"));

        if (grade.getGradeDate() == null) {
            grade.setGradeDate(LocalDate.now());
        }

        boolean absent = Boolean.TRUE.equals(grade.getIsAbsent());
        if (absent) {
            grade.setGradeStatusId(catalogs.requireId("grade_statuses", "AUSENTE"));
            if (grade.getScore() == null) {
                grade.setScore(BigDecimal.ONE);
            }
        } else {
            if (grade.getScore() == null) {
                throw new BadRequestException("La nota es obligatoria");
            }
            double score = grade.getScore().doubleValue();
            if (score < 1.0 || score > 7.0) {
                throw new BadRequestException("La nota debe estar entre 1.0 y 7.0");
            }
            if (grade.getGradeStatusId() == null) {
                grade.setGradeStatusId(catalogs.requireId("grade_statuses", "DEFINITIVA"));
            }
        }

        if (subject.getCourseId() != null) {
            Short activeStatusId = catalogs.requireId("enrollment_statuses", "ACTIVO");
            boolean enrolled = enrollmentRepository
                    .findByStudentIdAndCourseIdAndEnrollmentStatusId(
                            grade.getStudentId(), subject.getCourseId(), activeStatusId)
                    .stream()
                    .findAny()
                    .isPresent();
            if (!enrolled) {
                throw new BadRequestException("El estudiante no está matriculado en el curso de esta evaluación");
            }
        }

        List<Grade> existingGrades = gradeRepository.findByStudentId(grade.getStudentId());
        for (Grade existing : existingGrades) {
            if (existing.getEvaluationId().equals(grade.getEvaluationId())
                    && (excludeId == null || !existing.getId().equals(excludeId))) {
                throw new ConflictException("Ya existe una nota para este estudiante en esta evaluación");
            }
        }
    }

    public void requireFound(Optional<?> entity, String message) {
        if (entity.isEmpty()) {
            throw new NotFoundException(message);
        }
    }

    private void validatePastDate(LocalDate date, String label) {
        if (date.isAfter(LocalDate.now())) {
            throw new BadRequestException(label + " no puede ser futura");
        }
    }

    private void validateNotFutureDate(LocalDate date, String label) {
        if (date.isAfter(LocalDate.now())) {
            throw new BadRequestException(label + " no puede ser futura");
        }
    }

    private <T> void ensureUniqueRut(Optional<T> existing, Long excludeId, String message) {
        existing.ifPresent(entity -> {
            Long id = extractId(entity);
            if (excludeId == null || !excludeId.equals(id)) {
                throw new ConflictException(message);
            }
        });
    }

    private <T> void ensureUniqueEmail(Optional<T> existing, Long excludeId, String message) {
        ensureUniqueRut(existing, excludeId, message);
    }

    private Long extractId(Object entity) {
        if (entity instanceof Student student) return student.getId();
        if (entity instanceof Guardian guardian) return guardian.getId();
        if (entity instanceof Teacher teacher) return teacher.getId();
        throw new IllegalStateException("Tipo no soportado para validación de duplicados");
    }
}
