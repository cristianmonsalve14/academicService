package cl.duoc.libroDigital.academicService.service.impl;

import cl.duoc.libroDigital.academicService.model.Enrollment;
import cl.duoc.libroDigital.academicService.repository.EnrollmentRepository;
import cl.duoc.libroDigital.academicService.service.EnrollmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Override
    public Enrollment createEnrollment(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    @Override
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    @Override
    public Optional<Enrollment> getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id);
    }

    @Override
    public Enrollment updateEnrollment(Long id, Enrollment enrollment) {
        Optional<Enrollment> existingEnrollment = enrollmentRepository.findById(id);
        if (existingEnrollment.isPresent()) {
            Enrollment updatedEnrollment = existingEnrollment.get();
            if (enrollment.getStudentId() != null) {
                updatedEnrollment.setStudentId(enrollment.getStudentId());
            }
            if (enrollment.getCourseId() != null) {
                updatedEnrollment.setCourseId(enrollment.getCourseId());
            }
            if (enrollment.getEnrollmentDate() != null) {
                updatedEnrollment.setEnrollmentDate(enrollment.getEnrollmentDate());
            }
            return enrollmentRepository.save(updatedEnrollment);
        }
        return null;
    }

    @Override
    public void deleteEnrollment(Long id) {
        enrollmentRepository.deleteById(id);
    }
}
