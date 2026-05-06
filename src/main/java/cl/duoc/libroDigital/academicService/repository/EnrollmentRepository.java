package cl.duoc.libroDigital.academicService.repository;

import cl.duoc.libroDigital.academicService.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
}
