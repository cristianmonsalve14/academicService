package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.model.Subject;

import java.util.List;
import java.util.Optional;

public interface SubjectService {

    Subject createSubject(Subject subject);

    List<Subject> getAllSubjects();

    Optional<Subject> getSubjectById(Long id);

    Subject updateSubject(Long id, Subject subject);

    void deleteSubject(Long id);
}
