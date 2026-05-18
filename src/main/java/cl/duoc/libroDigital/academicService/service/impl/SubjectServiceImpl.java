package cl.duoc.libroDigital.academicService.service.impl;

import cl.duoc.libroDigital.academicService.model.Subject;
import cl.duoc.libroDigital.academicService.repository.SubjectRepository;
import cl.duoc.libroDigital.academicService.service.SubjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public Subject createSubject(Subject subject) {
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

            if (subject.getSubjectCode() != null) {
                existingSubject.setSubjectCode(subject.getSubjectCode());
            }

            if (subject.getSubjectName() != null) {
                existingSubject.setSubjectName(subject.getSubjectName());
            }

            if (subject.getDescription() != null) {
                existingSubject.setDescription(subject.getDescription());
            }

            if (subject.getSubjectType() != null) {
                existingSubject.setSubjectType(subject.getSubjectType());
            }

            if (subject.getWeeklyHours() != null) {
                existingSubject.setWeeklyHours(subject.getWeeklyHours());
            }

            if (subject.getTeacherId() != null) {
                existingSubject.setTeacherId(subject.getTeacherId());
            }

            if (subject.getCourseId() != null) {
                existingSubject.setCourseId(subject.getCourseId());
            }

            return subjectRepository.save(existingSubject);

        }).orElseThrow(() -> new RuntimeException("Subject not found with id " + id));
    }

    @Override
    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }

    // ===== MÉTODOS EXTRA =====

    @Override
    public Optional<Subject> getSubjectByCode(String subjectCode) {
        return subjectRepository.findBySubjectCode(subjectCode);
    }

    @Override
    public List<Subject> getSubjectsByTeacher(Long teacherId) {
        return subjectRepository.findByTeacherId(teacherId);
    }
}
