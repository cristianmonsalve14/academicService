package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.model.Enrollment;

import java.util.List;
import java.util.Optional;

public interface EnrollmentService {

    Enrollment createEnrollment(Enrollment enrollment);

    List<Enrollment> getAllEnrollments();

    Optional<Enrollment> getEnrollmentById(Long id);

    Enrollment updateEnrollment(Long id, Enrollment enrollment);

    void deleteEnrollment(Long id);
}

