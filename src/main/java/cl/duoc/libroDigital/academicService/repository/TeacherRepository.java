package cl.duoc.libroDigital.academicService.repository;

import cl.duoc.libroDigital.academicService.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByRut(String rut);

    Optional<Teacher> findByEmail(String email);

    Optional<Teacher> findByEmailIgnoreCase(String email);

    Optional<Teacher> findByEmployeeNumber(String employeeNumber);

    // Buscar profesores por estado
    List<Teacher> findByTeacherStatusId(Short teacherStatusId);

    Optional<Teacher> findByAuthUsername(String authUsername);

    Optional<Teacher> findByUserId(Long userId);
}
