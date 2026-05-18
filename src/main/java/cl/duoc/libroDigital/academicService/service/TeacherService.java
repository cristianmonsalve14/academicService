package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.model.Teacher;

import java.util.List;
import java.util.Optional;

public interface TeacherService {

    // Crear profesor
    Teacher createTeacher(Teacher teacher);

    // Listar todos
    List<Teacher> getAllTeachers();

    // Buscar por ID
    Optional<Teacher> getTeacherById(Long id);

    // Actualizar
    Teacher updateTeacher(Long id, Teacher teacher);

    // Eliminar
    void deleteTeacher(Long id);

    // ===== MÉTODOS PRO =====

    Optional<Teacher> getTeacherByRut(String rut);

    Optional<Teacher> getTeacherByEmail(String email);

    List<Teacher> getTeachersByStatus(String teacherStatus);

}
