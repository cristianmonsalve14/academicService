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

        return enrollmentRepository.findById(id).map(existing -> {

            if (enrollment.getStudentId() != null) {
                existing.setStudentId(enrollment.getStudentId());
            }

            if (enrollment.getCourseId() != null) {
                existing.setCourseId(enrollment.getCourseId());
            }

            if (enrollment.getEnrollmentDate() != null) {
                existing.setEnrollmentDate(enrollment.getEnrollmentDate());
            }

            if (enrollment.getAcademicYear() != null) {
                existing.setAcademicYear(enrollment.getAcademicYear());
            }

            if (enrollment.getEnrollmentStatus() != null) {
                existing.setEnrollmentStatus(enrollment.getEnrollmentStatus());
            }

            if (enrollment.getIsRegular() != null) {
                existing.setIsRegular(enrollment.getIsRegular());
            }

            if (enrollment.getObservations() != null) {
                existing.setObservations(enrollment.getObservations());
            }

            return enrollmentRepository.save(existing);

        }).orElseThrow(() ->
                new RuntimeException("Matrícula no encontrada con id " + id)
        );
    }

    @Override
    public void deleteEnrollment(Long id) {
        enrollmentRepository.deleteById(id);
    }

    // ===== MÉTODOS EXTRA =====

    @Override
    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    @Override
    public List<Enrollment> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    @Override
    public List<Enrollment> getEnrollmentsByStatus(String enrollmentStatus) {
        return enrollmentRepository.findByEnrollmentStatus(enrollmentStatus);
    }
}