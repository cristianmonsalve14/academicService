package cl.duoc.libroDigital.academicService.repository;

import cl.duoc.libroDigital.academicService.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
}
