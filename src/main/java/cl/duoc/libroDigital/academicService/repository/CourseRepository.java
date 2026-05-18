package cl.duoc.libroDigital.academicService.repository;

import cl.duoc.libroDigital.academicService.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
