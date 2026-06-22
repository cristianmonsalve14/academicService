package cl.duoc.libroDigital.academicService.service.impl;



import cl.duoc.libroDigital.academicService.exception.NotFoundException;

import cl.duoc.libroDigital.academicService.model.Enrollment;

import cl.duoc.libroDigital.academicService.repository.EnrollmentRepository;

import cl.duoc.libroDigital.academicService.service.CatalogLookupService;

import cl.duoc.libroDigital.academicService.service.EnrollmentNumberService;

import cl.duoc.libroDigital.academicService.service.EnrollmentService;

import cl.duoc.libroDigital.academicService.validation.AcademicEntityValidator;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;



import java.util.List;

import java.util.Optional;



@Service

public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CatalogLookupService catalogs;
    private final EnrollmentNumberService enrollmentNumberService;
    private final AcademicEntityValidator validator;

    public EnrollmentServiceImpl(
            EnrollmentRepository enrollmentRepository,
            CatalogLookupService catalogs,
            EnrollmentNumberService enrollmentNumberService,
            AcademicEntityValidator validator) {
        this.enrollmentRepository = enrollmentRepository;
        this.catalogs = catalogs;
        this.enrollmentNumberService = enrollmentNumberService;
        this.validator = validator;
    }

    @Override

    @Transactional

    public Enrollment createEnrollment(Enrollment enrollment) {

        validator.validateEnrollmentForSave(enrollment, null);

        Enrollment saved = enrollmentRepository.save(enrollment);

        enrollmentNumberService.assignIfNeeded(saved);

        return saved;

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

            if (enrollment.getStudentId() != null) existing.setStudentId(enrollment.getStudentId());

            if (enrollment.getCourseId() != null) existing.setCourseId(enrollment.getCourseId());

            if (enrollment.getEnrollmentDate() != null) existing.setEnrollmentDate(enrollment.getEnrollmentDate());

            if (enrollment.getAcademicYearId() != null) existing.setAcademicYearId(enrollment.getAcademicYearId());

            if (enrollment.getEnrollmentStatusId() != null) existing.setEnrollmentStatusId(enrollment.getEnrollmentStatusId());

            if (enrollment.getIsRegular() != null) existing.setIsRegular(enrollment.getIsRegular());

            if (enrollment.getObservations() != null) existing.setObservations(enrollment.getObservations());



            validator.validateEnrollmentForSave(existing, id);

            return enrollmentRepository.save(existing);

        }).orElseThrow(() -> new NotFoundException("Matrícula no encontrada con id " + id));

    }



    @Override

    public void deleteEnrollment(Long id) {

        enrollmentRepository.deleteById(id);

    }



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

        return enrollmentRepository.findByEnrollmentStatusId(catalogs.requireId("enrollment_statuses", enrollmentStatus));

    }

}

