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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcademicEntityValidatorTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private GuardianRepository guardianRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private EvaluationRepository evaluationRepository;
    @Mock
    private GradeRepository gradeRepository;
    @Mock
    private CatalogLookupService catalogs;

    @InjectMocks
    private AcademicEntityValidator validator;

    @Test
    void validateStudentForSave_successNormalizesAndValidates() {
        Student student = new Student();
        student.setRut("12345678-5");
        student.setFirstName(" Ana ");
        student.setLastName("Pérez");
        student.setMotherLastName("Gómez");
        student.setEmail(" ANA@MAIL.CL ");
        student.setPhone("+56 9 1234 5678");
        student.setDateOfBirth(LocalDate.now().minusYears(10));
        student.setGuardianId(70L);

        when(guardianRepository.existsById(70L)).thenReturn(true);
        when(studentRepository.findByRut("12.345.678-5")).thenReturn(Optional.empty());
        when(studentRepository.findByEmail("ana@mail.cl")).thenReturn(Optional.empty());

        validator.validateStudentForSave(student, null);

        assertEquals("12.345.678-5", student.getRut());
        assertEquals("Ana", student.getFirstName());
        assertEquals("ana@mail.cl", student.getEmail());
        assertEquals("912345678", student.getPhone());
    }

    @Test
    void validateStudentForSave_duplicateRutThrowsConflict() {
        Student student = new Student();
        student.setRut("12.345.678-5");
        student.setFirstName("Ana");
        student.setLastName("Pérez");
        student.setMotherLastName("Gómez");
        student.setEmail("ana@mail.cl");

        Student existing = new Student();
        existing.setId(200L);
        when(studentRepository.findByRut("12.345.678-5")).thenReturn(Optional.of(existing));

        assertThrows(ConflictException.class, () -> validator.validateStudentForSave(student, null));
    }

    @Test
    void validateGuardianForSave_success() {
        Guardian guardian = new Guardian();
        guardian.setRut("12345678-5");
        guardian.setFirstName("Marta");
        guardian.setLastName("Rojas");
        guardian.setEmail("MARTA@MAIL.CL");
        guardian.setPhone("+56 9 1111 1111");
        guardian.setEmergencyPhone("+56 2 2345 6789");

        when(guardianRepository.findByRut("12.345.678-5")).thenReturn(Optional.empty());
        when(guardianRepository.findByEmail("marta@mail.cl")).thenReturn(Optional.empty());

        validator.validateGuardianForSave(guardian, null);

        assertEquals("12.345.678-5", guardian.getRut());
        assertEquals("marta@mail.cl", guardian.getEmail());
        assertEquals("911111111", guardian.getPhone());
    }

    @Test
    void validateTeacherForSave_success() {
        Teacher teacher = new Teacher();
        teacher.setRut("12345678-5");
        teacher.setFirstName("Luis");
        teacher.setLastName("Soto");
        teacher.setEmail("LUIS@MAIL.CL");
        teacher.setPhone("+56 9 9999 9999");

        when(teacherRepository.findByRut("12.345.678-5")).thenReturn(Optional.empty());
        when(teacherRepository.findByEmail("luis@mail.cl")).thenReturn(Optional.empty());

        validator.validateTeacherForSave(teacher, null);

        assertEquals("12.345.678-5", teacher.getRut());
        assertEquals("luis@mail.cl", teacher.getEmail());
        assertEquals("999999999", teacher.getPhone());
    }

    @Test
    void validateCourseForSave_missingAcademicYearThrows() {
        Course course = new Course();
        course.setName("3 Medio A");
        course.setAcademicYearId(null);

        assertThrows(BadRequestException.class, () -> validator.validateCourseForSave(course));
    }

    @Test
    void validateEnrollmentForSave_successAssignsDefaultStatus() {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(10L);
        enrollment.setCourseId(20L);
        enrollment.setEnrollmentStatusId(null);
        enrollment.setEnrollmentDate(LocalDate.now());

        when(studentRepository.existsById(10L)).thenReturn(true);
        when(courseRepository.existsById(20L)).thenReturn(true);
        when(catalogs.requireId("enrollment_statuses", "ACTIVO")).thenReturn((short) 1);
        when(enrollmentRepository.findByStudentIdAndCourseIdAndEnrollmentStatusId(10L, 20L, (short) 1))
                .thenReturn(List.of());

        validator.validateEnrollmentForSave(enrollment, null);

        assertEquals((short) 1, enrollment.getEnrollmentStatusId());
    }

    @Test
    void validateSubjectForSave_tempNameFails() {
        Subject subject = new Subject();
        subject.setSubjectCode("MAT-01");
        subject.setSubjectName("TEMP");
        subject.setCourseId(5L);

        assertThrows(BadRequestException.class, () -> validator.validateSubjectForSave(subject, null));
    }

    @Test
    void validateEvaluationForSave_success() {
        Evaluation evaluation = new Evaluation();
        evaluation.setName("Prueba 1");
        evaluation.setSubjectId(40L);
        evaluation.setDate(LocalDate.now());
        evaluation.setEvaluationTypeId((short) 1);
        evaluation.setWeight(40.0);
        evaluation.setMaxScore(7.0);

        when(subjectRepository.findById(40L)).thenReturn(Optional.of(new Subject()));

        assertDoesNotThrow(() -> validator.validateEvaluationForSave(evaluation, null));
    }

    @Test
    void validateGradeForSave_absentAssignsAbsentStatusAndDefaultScore() {
        Grade grade = new Grade();
        grade.setStudentId(10L);
        grade.setEvaluationId(20L);
        grade.setIsAbsent(true);
        grade.setScore(null);

        Evaluation evaluation = new Evaluation();
        evaluation.setId(20L);
        evaluation.setSubjectId(30L);
        Subject subject = new Subject();
        subject.setId(30L);
        subject.setCourseId(40L);

        when(studentRepository.existsById(10L)).thenReturn(true);
        when(evaluationRepository.findById(20L)).thenReturn(Optional.of(evaluation));
        when(subjectRepository.findById(30L)).thenReturn(Optional.of(subject));
        when(catalogs.requireId("grade_statuses", "AUSENTE")).thenReturn((short) 6);
        when(catalogs.requireId("enrollment_statuses", "ACTIVO")).thenReturn((short) 1);
        when(enrollmentRepository.findByStudentIdAndCourseIdAndEnrollmentStatusId(10L, 40L, (short) 1))
                .thenReturn(List.of(new Enrollment()));
        when(gradeRepository.findByStudentId(10L)).thenReturn(List.of());

        validator.validateGradeForSave(grade, null);

        assertEquals((short) 6, grade.getGradeStatusId());
        assertEquals(new BigDecimal("1"), grade.getScore());
        assertNotNull(grade.getGradeDate());
    }

    @Test
    void validateGradeForSave_normalScoreAssignsDefaultStatus() {
        Grade grade = new Grade();
        grade.setStudentId(10L);
        grade.setEvaluationId(20L);
        grade.setScore(new BigDecimal("6.0"));
        grade.setIsAbsent(false);
        grade.setGradeStatusId(null);

        Evaluation evaluation = new Evaluation();
        evaluation.setId(20L);
        evaluation.setSubjectId(30L);
        Subject subject = new Subject();
        subject.setId(30L);
        subject.setCourseId(40L);

        when(studentRepository.existsById(10L)).thenReturn(true);
        when(evaluationRepository.findById(20L)).thenReturn(Optional.of(evaluation));
        when(subjectRepository.findById(30L)).thenReturn(Optional.of(subject));
        when(catalogs.requireId("grade_statuses", "DEFINITIVA")).thenReturn((short) 2);
        when(catalogs.requireId("enrollment_statuses", "ACTIVO")).thenReturn((short) 1);
        when(enrollmentRepository.findByStudentIdAndCourseIdAndEnrollmentStatusId(10L, 40L, (short) 1))
                .thenReturn(List.of(new Enrollment()));
        when(gradeRepository.findByStudentId(10L)).thenReturn(List.of());

        validator.validateGradeForSave(grade, null);

        assertEquals((short) 2, grade.getGradeStatusId());
        assertEquals(new BigDecimal("6.0"), grade.getScore());
    }

    @Test
    void requireFound_throwsWhenEntityMissing() {
        assertThrows(NotFoundException.class, () -> validator.requireFound(Optional.empty(), "No existe"));
    }
}
