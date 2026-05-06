package cl.duoc.libroDigital.academicService.repository;

import cl.duoc.libroDigital.academicService.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}

