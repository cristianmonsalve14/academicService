package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.exception.NotFoundException;
import cl.duoc.libroDigital.academicService.model.Grade;
import cl.duoc.libroDigital.academicService.repository.GradeRepository;
import cl.duoc.libroDigital.academicService.service.impl.GradeServiceImpl;
import cl.duoc.libroDigital.academicService.validation.AcademicEntityValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeServiceImplTest {

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private AcademicEntityValidator validator;

    @InjectMocks
    private GradeServiceImpl gradeService;

    @Test
    void createGrade_validatesAndSaves() {
        Grade grade = new Grade();
        grade.setStudentId(1L);
        grade.setEvaluationId(2L);
        grade.setScore(new BigDecimal("6.5"));
        grade.setGradeDate(LocalDate.of(2026, 6, 1));
        when(gradeRepository.save(grade)).thenReturn(grade);

        Grade saved = gradeService.createGrade(grade);

        assertSame(grade, saved);
        verify(validator).validateGradeForSave(grade, null);
        verify(gradeRepository).save(grade);
    }

    @Test
    void getGradeById_returnsGrade() {
        Grade grade = new Grade();
        grade.setId(1L);
        grade.setScore(new BigDecimal("5.8"));
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(grade));

        Optional<Grade> result = gradeService.getGradeById(1L);

        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("5.8"), result.get().getScore());
    }

    @Test
    void updateGrade_notFoundThrows() {
        when(gradeRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> gradeService.updateGrade(99L, new Grade()));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void getGradesByStudentId_delegatesToRepository() {
        Grade grade = new Grade();
        grade.setStudentId(7L);
        when(gradeRepository.findByStudentId(7L)).thenReturn(List.of(grade));

        List<Grade> result = gradeService.getGradesByStudentId(7L);

        assertEquals(1, result.size());
        assertEquals(7L, result.getFirst().getStudentId());
    }
}
