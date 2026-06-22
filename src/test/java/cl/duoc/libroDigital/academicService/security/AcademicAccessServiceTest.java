package cl.duoc.libroDigital.academicService.security;

import cl.duoc.libroDigital.academicService.exception.ForbiddenException;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcademicAccessServiceTest {

    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private GuardianRepository guardianRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private EvaluationRepository evaluationRepository;
    @Mock
    private GradeRepository gradeRepository;

    @InjectMocks
    private AcademicAccessService accessService;

    @BeforeEach
    void setUp() {
        SecurityTestSupport.clear();
    }

    @AfterEach
    void tearDown() {
        SecurityTestSupport.clear();
    }

    @Test
    void isTeacher_returnsTrueForDocente() {
        SecurityTestSupport.authenticateAs("teacher", 10L, "teacher@duoc.cl", "DOCENTE");
        assertTrue(accessService.isTeacher());
        assertFalse(accessService.isGuardian());
        assertFalse(accessService.isStudent());
    }

    @Test
    void isGuardian_returnsTrueForApoderado() {
        SecurityTestSupport.authenticateAs("guardian", 11L, "guardian@duoc.cl", "APODERADO");
        assertTrue(accessService.isGuardian());
    }

    @Test
    void isStudent_returnsTrueForEstudiante() {
        SecurityTestSupport.authenticateAs("student", 12L, "student@duoc.cl", "ESTUDIANTE");
        assertTrue(accessService.isStudent());
    }

    @Test
    void requireTeacherOrAdmin_throwsForGuardian() {
        SecurityTestSupport.authenticateAs("guardian", 11L, "guardian@duoc.cl", "APODERADO");
        assertThrows(ForbiddenException.class, () -> accessService.requireTeacherOrAdmin());
    }

    @Test
    void requireFamilyOrStaffRead_throwsWithoutKnownRole() {
        SecurityTestSupport.clear();
        assertThrows(ForbiddenException.class, () -> accessService.requireFamilyOrStaffRead());
    }

    @Test
    void currentTeacher_resolvesByUserId() {
        SecurityTestSupport.authenticateAs("teacher", 21L, "teacher@duoc.cl", "DOCENTE");
        Teacher teacher = teacher(101L, 21L);
        when(teacherRepository.findByUserId(21L)).thenReturn(Optional.of(teacher));

        Optional<Teacher> result = accessService.currentTeacher();

        assertTrue(result.isPresent());
        assertEquals(101L, result.get().getId());
    }

    @Test
    void currentStudent_resolvesByEmailFallback() {
        SecurityTestSupport.authenticateAs("student", null, "student@duoc.cl", "ESTUDIANTE");
        Student student = student(102L, null, "student@duoc.cl", null);
        when(studentRepository.findByEmailIgnoreCase("student@duoc.cl")).thenReturn(Optional.of(student));

        Optional<Student> result = accessService.currentStudent();

        assertTrue(result.isPresent());
        assertEquals(102L, result.get().getId());
    }

    @Test
    void currentGuardian_resolvesByUserId() {
        SecurityTestSupport.authenticateAs("guardian", 31L, "guardian@duoc.cl", "APODERADO");
        Guardian guardian = guardian(201L, 31L, "guardian@duoc.cl");
        when(guardianRepository.findByUserId(31L)).thenReturn(Optional.of(guardian));

        Optional<Guardian> result = accessService.currentGuardian();

        assertTrue(result.isPresent());
        assertEquals(201L, result.get().getId());
    }

    @Test
    void teacherSubjectIds_returnsTeacherSubjects() {
        when(subjectRepository.findByTeacherId(15L)).thenReturn(List.of(
                subject(1L, 30L, 15L),
                subject(2L, 31L, 15L)));

        assertEquals(List.of(1L, 2L), accessService.teacherSubjectIds(15L));
    }

    @Test
    void teacherCourseIds_combinesOwnedAndHeadTeacherCourses() {
        when(subjectRepository.findByTeacherId(15L)).thenReturn(List.of(
                subject(1L, 30L, 15L),
                subject(2L, 31L, 15L)));
        when(courseRepository.findAll()).thenReturn(List.of(
                course(40L, 15L),
                course(50L, 99L),
                course(30L, 15L)));

        Set<Long> courseIds = accessService.teacherCourseIds(15L);

        assertEquals(Set.of(30L, 31L, 40L), courseIds);
    }

    @Test
    void teacherStudentIds_returnsStudentsFromTeacherCourses() {
        when(subjectRepository.findByTeacherId(15L)).thenReturn(List.of(subject(1L, 30L, 15L)));
        when(courseRepository.findAll()).thenReturn(List.of(course(40L, 15L)));
        when(enrollmentRepository.findAll()).thenReturn(List.of(
                enrollment(501L, 30L),
                enrollment(502L, 40L),
                enrollment(503L, 99L)));

        Set<Long> studentIds = accessService.teacherStudentIds(15L);

        assertEquals(Set.of(501L, 502L), studentIds);
    }

    @Test
    void guardianCourseIds_returnsCoursesForGuardianWards() {
        when(studentRepository.findByGuardianId(201L)).thenReturn(List.of(
                student(501L, null, null, 201L),
                student(502L, null, null, 201L)));
        when(enrollmentRepository.findAll()).thenReturn(List.of(
                enrollment(501L, 30L),
                enrollment(502L, 31L),
                enrollment(999L, 99L)));

        Set<Long> courseIds = accessService.guardianCourseIds(201L);

        assertEquals(Set.of(30L, 31L), courseIds);
    }

    @Test
    void ensureCanReadSubject_teacherOwnSubjectAllowed() {
        SecurityTestSupport.authenticateAs("teacher", 21L, "teacher@duoc.cl", "DOCENTE");
        when(teacherRepository.findByUserId(21L)).thenReturn(Optional.of(teacher(101L, 21L)));
        when(subjectRepository.findById(700L)).thenReturn(Optional.of(subject(700L, 30L, 101L)));

        assertDoesNotThrow(() -> accessService.ensureCanReadSubject(700L));
    }

    @Test
    void ensureCanReadSubject_studentEnrolledAllowed() {
        SecurityTestSupport.authenticateAs("student", 41L, "student@duoc.cl", "ESTUDIANTE");
        when(studentRepository.findByUserId(41L)).thenReturn(Optional.of(student(102L, 41L, "student@duoc.cl", null)));
        when(subjectRepository.findById(701L)).thenReturn(Optional.of(subject(701L, 31L, 999L)));
        when(enrollmentRepository.findByStudentId(102L)).thenReturn(List.of(enrollment(102L, 31L)));

        assertDoesNotThrow(() -> accessService.ensureCanReadSubject(701L));
    }

    @Test
    void ensureCanReadSubject_guardianWithWardInCourseAllowed() {
        SecurityTestSupport.authenticateAs("guardian", 31L, "guardian@duoc.cl", "APODERADO");
        when(guardianRepository.findByUserId(31L)).thenReturn(Optional.of(guardian(201L, 31L, "guardian@duoc.cl")));
        when(studentRepository.findByGuardianId(201L)).thenReturn(List.of(student(501L, null, null, 201L)));
        when(subjectRepository.findById(702L)).thenReturn(Optional.of(subject(702L, 32L, 999L)));
        when(enrollmentRepository.findAll()).thenReturn(List.of(enrollment(501L, 32L)));

        assertDoesNotThrow(() -> accessService.ensureCanReadSubject(702L));
    }

    @Test
    void ensureCanReadSubject_teacherWithoutOwnershipThrows() {
        SecurityTestSupport.authenticateAs("teacher", 21L, "teacher@duoc.cl", "DOCENTE");
        when(teacherRepository.findByUserId(21L)).thenReturn(Optional.of(teacher(101L, 21L)));
        when(subjectRepository.findById(703L)).thenReturn(Optional.of(subject(703L, 33L, 999L)));

        assertThrows(ForbiddenException.class, () -> accessService.ensureCanReadSubject(703L));
    }

    @Test
    void ensureCanReadEvaluation_teacherAllowed() {
        SecurityTestSupport.authenticateAs("teacher", 21L, "teacher@duoc.cl", "DOCENTE");
        when(teacherRepository.findByUserId(21L)).thenReturn(Optional.of(teacher(101L, 21L)));
        when(evaluationRepository.findById(801L)).thenReturn(Optional.of(evaluation(801L, 700L)));
        when(subjectRepository.findById(700L)).thenReturn(Optional.of(subject(700L, 30L, 101L)));

        assertDoesNotThrow(() -> accessService.ensureCanReadEvaluation(801L));
    }

    @Test
    void ensureCanReadEvaluation_studentThrowsForbidden() {
        SecurityTestSupport.authenticateAs("student", 41L, "student@duoc.cl", "ESTUDIANTE");
        when(evaluationRepository.findById(801L)).thenReturn(Optional.of(evaluation(801L, 700L)));

        assertThrows(ForbiddenException.class, () -> accessService.ensureCanReadEvaluation(801L));
    }

    @Test
    void ensureCanManageEvaluation_teacherAllowedWhenSubjectOwned() {
        SecurityTestSupport.authenticateAs("teacher", 21L, "teacher@duoc.cl", "DOCENTE");
        when(teacherRepository.findByUserId(21L)).thenReturn(Optional.of(teacher(101L, 21L)));
        when(evaluationRepository.findById(802L)).thenReturn(Optional.of(evaluation(802L, 700L)));
        when(subjectRepository.findById(700L)).thenReturn(Optional.of(subject(700L, 30L, 101L)));

        assertDoesNotThrow(() -> accessService.ensureCanManageEvaluation(802L));
    }

    @Test
    void ensureCanManageEvaluation_adminThrowsByPedagogicalWriteRule() {
        SecurityTestSupport.authenticateAs("admin", 1L, "admin@duoc.cl", "ADMINISTRADOR");
        assertThrows(ForbiddenException.class, () -> accessService.ensureCanManageEvaluation(802L));
    }

    @Test
    void ensureCanReadGrade_teacherAllowed() {
        SecurityTestSupport.authenticateAs("teacher", 21L, "teacher@duoc.cl", "DOCENTE");
        when(teacherRepository.findByUserId(21L)).thenReturn(Optional.of(teacher(101L, 21L)));
        when(gradeRepository.findById(901L)).thenReturn(Optional.of(grade(901L, 501L, 801L)));
        when(evaluationRepository.findById(801L)).thenReturn(Optional.of(evaluation(801L, 700L)));
        when(subjectRepository.findById(700L)).thenReturn(Optional.of(subject(700L, 30L, 101L)));

        assertDoesNotThrow(() -> accessService.ensureCanReadGrade(901L));
    }

    @Test
    void ensureCanReadGrade_studentSelfAllowed() {
        SecurityTestSupport.authenticateAs("student", 41L, "student@duoc.cl", "ESTUDIANTE");
        when(studentRepository.findByUserId(41L)).thenReturn(Optional.of(student(501L, 41L, "student@duoc.cl", null)));
        when(gradeRepository.findById(902L)).thenReturn(Optional.of(grade(902L, 501L, 801L)));

        assertDoesNotThrow(() -> accessService.ensureCanReadGrade(902L));
    }

    @Test
    void ensureCanReadGrade_guardianChildAllowed() {
        SecurityTestSupport.authenticateAs("guardian", 31L, "guardian@duoc.cl", "APODERADO");
        when(guardianRepository.findByUserId(31L)).thenReturn(Optional.of(guardian(201L, 31L, "guardian@duoc.cl")));
        when(studentRepository.findByGuardianId(201L)).thenReturn(List.of(student(501L, null, null, 201L)));
        when(gradeRepository.findById(903L)).thenReturn(Optional.of(grade(903L, 501L, 801L)));

        assertDoesNotThrow(() -> accessService.ensureCanReadGrade(903L));
    }

    @Test
    void ensureCanReadCourse_studentAllowed() {
        SecurityTestSupport.authenticateAs("student", 41L, "student@duoc.cl", "ESTUDIANTE");
        when(studentRepository.findByUserId(41L)).thenReturn(Optional.of(student(501L, 41L, "student@duoc.cl", null)));
        when(enrollmentRepository.findByStudentId(501L)).thenReturn(List.of(enrollment(501L, 30L)));

        assertDoesNotThrow(() -> accessService.ensureCanReadCourse(30L));
    }

    @Test
    void ensureCanReadCourse_guardianAllowed() {
        SecurityTestSupport.authenticateAs("guardian", 31L, "guardian@duoc.cl", "APODERADO");
        when(guardianRepository.findByUserId(31L)).thenReturn(Optional.of(guardian(201L, 31L, "guardian@duoc.cl")));
        when(studentRepository.findByGuardianId(201L)).thenReturn(List.of(student(501L, null, null, 201L)));
        when(enrollmentRepository.findAll()).thenReturn(List.of(enrollment(501L, 30L)));

        assertDoesNotThrow(() -> accessService.ensureCanReadCourse(30L));
    }

    @Test
    void ensureCanReadCourse_teacherAllowed() {
        SecurityTestSupport.authenticateAs("teacher", 21L, "teacher@duoc.cl", "DOCENTE");
        when(teacherRepository.findByUserId(21L)).thenReturn(Optional.of(teacher(101L, 21L)));
        when(subjectRepository.findByTeacherId(101L)).thenReturn(List.of(subject(700L, 30L, 101L)));
        when(courseRepository.findAll()).thenReturn(List.of());

        assertDoesNotThrow(() -> accessService.ensureCanReadCourse(30L));
    }

    @Test
    void ensureCanReadTeacher_allowsSelfAndRejectsOtherTeacher() {
        SecurityTestSupport.authenticateAs("teacher", 21L, "teacher@duoc.cl", "DOCENTE");
        when(teacherRepository.findByUserId(21L)).thenReturn(Optional.of(teacher(101L, 21L)));

        assertDoesNotThrow(() -> accessService.ensureCanReadTeacher(101L));
        assertThrows(ForbiddenException.class, () -> accessService.ensureCanReadTeacher(999L));
    }

    @Test
    void ensureCanReadStudent_studentSelfAllowed() {
        SecurityTestSupport.authenticateAs("student", 41L, "student@duoc.cl", "ESTUDIANTE");
        when(studentRepository.findByUserId(41L)).thenReturn(Optional.of(student(501L, 41L, "student@duoc.cl", null)));

        assertDoesNotThrow(() -> accessService.ensureCanReadStudent(501L));
    }

    @Test
    void ensureCanReadStudent_teacherWithSharedCourseAllowed() {
        SecurityTestSupport.authenticateAs("teacher", 21L, "teacher@duoc.cl", "DOCENTE");
        when(teacherRepository.findByUserId(21L)).thenReturn(Optional.of(teacher(101L, 21L)));
        when(subjectRepository.findByTeacherId(101L)).thenReturn(List.of(subject(700L, 30L, 101L)));
        when(courseRepository.findAll()).thenReturn(List.of());
        when(enrollmentRepository.findAll()).thenReturn(List.of(enrollment(501L, 30L)));

        assertDoesNotThrow(() -> accessService.ensureCanReadStudent(501L));
    }

    @Test
    void ensureCanReadStudent_adminAlwaysAllowed() {
        SecurityTestSupport.authenticateAs("admin", 1L, "admin@duoc.cl", "ADMINISTRADOR");

        assertDoesNotThrow(() -> accessService.ensureCanReadStudent(99L));
        verifyNoInteractions(studentRepository, guardianRepository, teacherRepository, enrollmentRepository);
    }

    private static Teacher teacher(Long id, Long userId) {
        Teacher teacher = new Teacher();
        teacher.setId(id);
        teacher.setUserId(userId);
        teacher.setAuthUsername("teacher" + id);
        return teacher;
    }

    private static Student student(Long id, Long userId, String email, Long guardianId) {
        Student student = new Student();
        student.setId(id);
        student.setUserId(userId);
        student.setEmail(email);
        student.setGuardianId(guardianId);
        return student;
    }

    private static Guardian guardian(Long id, Long userId, String email) {
        Guardian guardian = new Guardian();
        guardian.setId(id);
        guardian.setUserId(userId);
        guardian.setEmail(email);
        return guardian;
    }

    private static Subject subject(Long id, Long courseId, Long teacherId) {
        Subject subject = new Subject();
        subject.setId(id);
        subject.setCourseId(courseId);
        subject.setTeacherId(teacherId);
        return subject;
    }

    private static Course course(Long id, Long headTeacherId) {
        Course course = new Course();
        course.setId(id);
        course.setHeadTeacherId(headTeacherId);
        return course;
    }

    private static Enrollment enrollment(Long studentId, Long courseId) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        return enrollment;
    }

    private static Evaluation evaluation(Long id, Long subjectId) {
        Evaluation evaluation = new Evaluation();
        evaluation.setId(id);
        evaluation.setSubjectId(subjectId);
        return evaluation;
    }

    private static Grade grade(Long id, Long studentId, Long evaluationId) {
        Grade grade = new Grade();
        grade.setId(id);
        grade.setStudentId(studentId);
        grade.setEvaluationId(evaluationId);
        return grade;
    }
}
