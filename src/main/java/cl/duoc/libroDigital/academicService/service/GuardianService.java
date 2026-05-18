package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.model.Guardian;

import java.util.List;
import java.util.Optional;

public interface GuardianService {

    Guardian createGuardian(Guardian guardian);

    List<Guardian> getAllGuardians();

    Optional<Guardian> getGuardianById(Long id);

    Guardian updateGuardian(Long id, Guardian guardian);

    void deleteGuardian(Long id);
}
