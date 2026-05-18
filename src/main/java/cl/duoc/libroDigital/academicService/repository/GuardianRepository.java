package cl.duoc.libroDigital.academicService.repository;

import cl.duoc.libroDigital.academicService.model.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuardianRepository extends JpaRepository<Guardian, Long> {
    
    Optional<Guardian> findByRut(String rut);
    
    Optional<Guardian> findByEmail(String email);
}
