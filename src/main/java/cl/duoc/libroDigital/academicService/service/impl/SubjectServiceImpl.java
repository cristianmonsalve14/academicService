package cl.duoc.libroDigital.academicService.service.impl;



import cl.duoc.libroDigital.academicService.exception.ConflictException;

import cl.duoc.libroDigital.academicService.exception.NotFoundException;

import cl.duoc.libroDigital.academicService.model.Subject;

import cl.duoc.libroDigital.academicService.repository.EvaluationRepository;

import cl.duoc.libroDigital.academicService.repository.SubjectRepository;

import cl.duoc.libroDigital.academicService.service.SubjectService;

import cl.duoc.libroDigital.academicService.validation.AcademicEntityValidator;

import org.springframework.stereotype.Service;



import java.util.List;

import java.util.Optional;



@Service

public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final EvaluationRepository evaluationRepository;
    private final AcademicEntityValidator validator;

    public SubjectServiceImpl(
            SubjectRepository subjectRepository,
            EvaluationRepository evaluationRepository,
            AcademicEntityValidator validator) {
        this.subjectRepository = subjectRepository;
        this.evaluationRepository = evaluationRepository;
        this.validator = validator;
    }

    @Override

    public Subject createSubject(Subject subject) {

        validator.validateSubjectForSave(subject, null);

        return subjectRepository.save(subject);

    }



    @Override

    public List<Subject> getAllSubjects() {

        return subjectRepository.findAll();

    }



    @Override

    public Optional<Subject> getSubjectById(Long id) {

        return subjectRepository.findById(id);

    }



    @Override

    public Subject updateSubject(Long id, Subject subject) {

        return subjectRepository.findById(id).map(existingSubject -> {

            if (subject.getSubjectCode() != null) existingSubject.setSubjectCode(subject.getSubjectCode());

            if (subject.getSubjectName() != null) existingSubject.setSubjectName(subject.getSubjectName());

            if (subject.getDescription() != null) existingSubject.setDescription(subject.getDescription());

            if (subject.getSubjectTypeId() != null) existingSubject.setSubjectTypeId(subject.getSubjectTypeId());

            if (subject.getWeeklyHours() != null) existingSubject.setWeeklyHours(subject.getWeeklyHours());

            if (subject.getTeacherId() != null) existingSubject.setTeacherId(subject.getTeacherId());

            if (subject.getCourseId() != null) existingSubject.setCourseId(subject.getCourseId());



            validator.validateSubjectForSave(existingSubject, id);

            return subjectRepository.save(existingSubject);

        }).orElseThrow(() -> new NotFoundException("Asignatura no encontrada con id " + id));

    }



    @Override

    public void deleteSubject(Long id) {

        if (!subjectRepository.existsById(id)) {

            throw new ConflictException("La asignatura no existe");

        }

        if (!evaluationRepository.findBySubjectId(id).isEmpty()) {

            throw new ConflictException(

                    "No se puede eliminar la asignatura porque tiene evaluaciones asociadas. Elimine primero las evaluaciones de esta asignatura.");

        }

        subjectRepository.deleteById(id);

    }



    @Override

    public Optional<Subject> getSubjectByCode(String subjectCode) {

        return subjectRepository.findBySubjectCode(subjectCode);

    }



    @Override

    public List<Subject> getSubjectsByTeacher(Long teacherId) {

        return subjectRepository.findByTeacherId(teacherId);

    }

}

