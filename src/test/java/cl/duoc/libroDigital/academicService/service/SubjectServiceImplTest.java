package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.exception.ConflictException;
import cl.duoc.libroDigital.academicService.exception.NotFoundException;
import cl.duoc.libroDigital.academicService.model.Evaluation;
import cl.duoc.libroDigital.academicService.model.Subject;
import cl.duoc.libroDigital.academicService.repository.EvaluationRepository;
import cl.duoc.libroDigital.academicService.repository.SubjectRepository;
import cl.duoc.libroDigital.academicService.service.impl.SubjectServiceImpl;
import cl.duoc.libroDigital.academicService.validation.AcademicEntityValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubjectServiceImplTest {

    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private EvaluationRepository evaluationRepository;
    @Mock
    private AcademicEntityValidator validator;

    @InjectMocks
    private SubjectServiceImpl subjectService;

    @Test
    void createSubject_validatesAndSaves() {
        Subject subject = new Subject();
        subject.setSubjectCode("MAT-01");
        subject.setSubjectName("Matemáticas");
        when(subjectRepository.save(subject)).thenReturn(subject);

        Subject saved = subjectService.createSubject(subject);

        assertSame(subject, saved);
        verify(validator).validateSubjectForSave(subject, null);
        verify(subjectRepository).save(subject);
    }

    @Test
    void updateSubject_appliesChangesAndSaves() {
        Subject existing = new Subject();
        existing.setId(20L);
        existing.setSubjectName("Historia");

        Subject update = new Subject();
        update.setSubjectName("Historia y Geografía");

        when(subjectRepository.findById(20L)).thenReturn(Optional.of(existing));
        when(subjectRepository.save(existing)).thenReturn(existing);

        Subject result = subjectService.updateSubject(20L, update);

        assertEquals("Historia y Geografía", result.getSubjectName());
        verify(validator).validateSubjectForSave(existing, 20L);
        verify(subjectRepository).save(existing);
    }

    @Test
    void updateSubject_notFoundThrows() {
        when(subjectRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> subjectService.updateSubject(99L, new Subject()));
    }

    @Test
    void deleteSubject_withEvaluationsThrowsConflict() {
        when(subjectRepository.existsById(33L)).thenReturn(true);
        when(evaluationRepository.findBySubjectId(33L)).thenReturn(List.of(new Evaluation()));

        assertThrows(ConflictException.class, () -> subjectService.deleteSubject(33L));
    }

    @Test
    void deleteSubject_withoutEvaluationsDeletes() {
        when(subjectRepository.existsById(34L)).thenReturn(true);
        when(evaluationRepository.findBySubjectId(34L)).thenReturn(List.of());

        subjectService.deleteSubject(34L);

        verify(subjectRepository).deleteById(34L);
    }
}
