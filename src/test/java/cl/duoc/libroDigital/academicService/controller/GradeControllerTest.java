package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.dto.GradeDTO;
import cl.duoc.libroDigital.academicService.model.Evaluation;
import cl.duoc.libroDigital.academicService.model.Grade;
import cl.duoc.libroDigital.academicService.model.Student;
import cl.duoc.libroDigital.academicService.model.Subject;
import cl.duoc.libroDigital.academicService.model.Teacher;
import cl.duoc.libroDigital.academicService.repository.EvaluationRepository;
import cl.duoc.libroDigital.academicService.repository.StudentRepository;
import cl.duoc.libroDigital.academicService.repository.SubjectRepository;
import cl.duoc.libroDigital.academicService.repository.TeacherRepository;
import cl.duoc.libroDigital.academicService.security.AcademicAccessService;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
import cl.duoc.libroDigital.academicService.service.GradeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GradeControllerTest {

    @Mock
    private GradeService gradeService;
    @Mock
    private EvaluationRepository evaluationRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private CatalogLookupService catalogs;
    @Mock
    private AcademicAccessService access;

    @InjectMocks
    private GradeController gradeController;

    @Test
    void createGrade_teacherRequestUsesCurrentTeacherAsGrader() {
        GradeDTO dto = new GradeDTO();
        dto.setStudentId(7L);
        dto.setEvaluationId(5L);
        dto.setScore(new BigDecimal("6.2"));
        dto.setGradeDate(LocalDate.of(2026, 6, 10));
        dto.setGradeStatus("DEFINITIVA");

        Grade created = new Grade();
        created.setId(80L);
        created.setStudentId(7L);
        created.setEvaluationId(5L);
        created.setScore(new BigDecimal("6.2"));
        created.setGradeDate(LocalDate.of(2026, 6, 10));
        created.setGradeStatusId((short) 1);
        created.setGradedByTeacherId(40L);

        Evaluation evaluation = new Evaluation();
        evaluation.setId(5L);
        evaluation.setName("Prueba 1");
        evaluation.setSubjectId(9L);
        Subject subject = new Subject();
        subject.setId(9L);
        subject.setSubjectName("Matemáticas");
        Student student = new Student();
        student.setFirstName("Ana");
        student.setLastName("Pérez");
        Teacher teacher = new Teacher();
        teacher.setFirstName("Luis");
        teacher.setLastName("Soto");

        when(access.isTeacher()).thenReturn(true);
        when(access.requireTeacherId()).thenReturn(40L);
        when(catalogs.requireId("grade_statuses", "DEFINITIVA")).thenReturn((short) 1);
        when(gradeService.createGrade(any(Grade.class))).thenReturn(created);
        when(catalogs.code("grade_statuses", (short) 1)).thenReturn("DEFINITIVA");
        when(evaluationRepository.findById(5L)).thenReturn(Optional.of(evaluation));
        when(subjectRepository.findById(9L)).thenReturn(Optional.of(subject));
        when(studentRepository.findById(7L)).thenReturn(Optional.of(student));
        when(teacherRepository.findById(40L)).thenReturn(Optional.of(teacher));

        ResponseEntity<GradeDTO> response = gradeController.createGrade(dto);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(80L, response.getBody().getId());
        assertEquals("Ana Pérez", response.getBody().getStudentName());
        assertEquals("Luis Soto", response.getBody().getGradedByTeacherName());
        verify(access).ensureCanManageGradeForEvaluation(5L);
        verify(access).ensureCanReadStudent(7L);

        ArgumentCaptor<Grade> captor = ArgumentCaptor.forClass(Grade.class);
        verify(gradeService).createGrade(captor.capture());
        assertEquals(40L, captor.getValue().getGradedByTeacherId());
    }

    @Test
    void getGradeById_returnsNotFoundWhenMissing() {
        when(gradeService.getGradeById(99L)).thenReturn(Optional.empty());

        ResponseEntity<GradeDTO> response = gradeController.getGradeById(99L);

        assertEquals(404, response.getStatusCode().value());
        verify(access, never()).ensureCanReadGrade(99L);
    }
}
