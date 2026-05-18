package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    // Crear estudiante
    Student createStudent(Student student);

    // Obtener todos los estudiantes
    List<Student> getAllStudents();

    // Obtener estudiante por ID
    Optional<Student> getStudentById(Long id);

    // Actualizar estudiante
    Student updateStudent(Long id, Student student);

    // Eliminar estudiante
    void deleteStudent(Long id);

    // ===== MÉTODOS EXTRA =====

    // Buscar por RUT
    Optional<Student> getStudentByRut(String rut);

    // Buscar por email
    Optional<Student> getStudentByEmail(String email);

    // Buscar por estado (activo, retirado, etc.)
    List<Student> getStudentsByStatus(String studentStatus);
}