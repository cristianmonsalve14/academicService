package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.model.Subject;

import java.util.List;

public interface SubjectService {

    Subject createSubject(Subject subject);

    List<Subject> getAllSubjects();
}
