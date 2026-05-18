package cl.duoc.libroDigital.academicService.repository;

import cl.duoc.libroDigital.academicService.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findBySubjectCode(String subjectCode);

    List<Subject> findByTeacherId(Long teacherId);
}
