package cl.duoc.libroDigital.academicService.repository;

import cl.duoc.libroDigital.academicService.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // ===== MÉTODOS PRO =====

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    List<Enrollment> findByEnrollmentStatusId(Short enrollmentStatusId);

    List<Enrollment> findByStudentIdAndCourseIdAndEnrollmentStatusId(
            Long studentId, Long courseId, Short enrollmentStatusId);
}