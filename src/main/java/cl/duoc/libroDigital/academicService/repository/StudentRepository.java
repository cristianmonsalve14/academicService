package cl.duoc.libroDigital.academicService.repository;

import cl.duoc.libroDigital.academicService.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByRut(String rut);

    Optional<Student> findByEmail(String email);

    List<Student> findByStudentStatus(String studentStatus);
}

