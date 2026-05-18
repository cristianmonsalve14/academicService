package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.model.Enrollment;

import java.util.List;
import java.util.Optional;

public interface EnrollmentService {

    // Crear matrícula
    Enrollment createEnrollment(Enrollment enrollment);

    // Listar todas
    List<Enrollment> getAllEnrollments();

    // Buscar por ID
    Optional<Enrollment> getEnrollmentById(Long id);

    // Actualizar
    Enrollment updateEnrollment(Long id, Enrollment enrollment);

    // Eliminar
    void deleteEnrollment(Long id);

    // ===== MÉTODOS=====

    // Buscar matrículas por estudiante
    List<Enrollment> getEnrollmentsByStudent(Long studentId);

    // Buscar matrículas por curso
    List<Enrollment> getEnrollmentsByCourse(Long courseId);

    // Buscar por estado (ACTIVO, RETIRADO, etc.)
    List<Enrollment> getEnrollmentsByStatus(String enrollmentStatus);
}