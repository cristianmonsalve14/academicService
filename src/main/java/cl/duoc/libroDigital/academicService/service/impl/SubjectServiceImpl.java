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
        Optional<Subject> existingSubject = subjectRepository.findById(id);
        if (existingSubject.isPresent()) {
            Subject updatedSubject = existingSubject.get();
            if (subject.getName() != null) {
                updatedSubject.setName(subject.getName());
            }
            if (subject.getCourseId() != null) {
                updatedSubject.setCourseId(subject.getCourseId());
            }
            return subjectRepository.save(updatedSubject);
        }
        return null;
    }

    @Override
    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }
}
