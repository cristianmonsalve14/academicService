package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.model.Enrollment;

import java.util.List;

public interface EnrollmentService {

    Enrollment createEnrollment(Enrollment enrollment);

    List<Enrollment> getAllEnrollments();
}

