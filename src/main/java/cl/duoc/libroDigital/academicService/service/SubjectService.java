package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.model.Subject;

import java.util.List;
import java.util.Optional;

public interface SubjectService {

    // Crear asignatura
    Subject createSubject(Subject subject);

    // Listar todas
    List<Subject> getAllSubjects();

    // Buscar por ID
    Optional<Subject> getSubjectById(Long id);

    // Actualizar
    Subject updateSubject(Long id, Subject subject);

    // Eliminar
    void deleteSubject(Long id);

    // ===== MÉTODOS EXTRA (PRO) =====

    Optional<Subject> getSubjectByCode(String subjectCode);

    List<Subject> getSubjectsByTeacher(Long teacherId);
}