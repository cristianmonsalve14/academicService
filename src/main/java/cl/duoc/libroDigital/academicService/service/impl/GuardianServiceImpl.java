package cl.duoc.libroDigital.academicService.service.impl;

import cl.duoc.libroDigital.academicService.model.Guardian;
import cl.duoc.libroDigital.academicService.repository.GuardianRepository;
import cl.duoc.libroDigital.academicService.service.GuardianService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GuardianServiceImpl implements GuardianService {

    @Autowired
    private GuardianRepository guardianRepository;

    @Override
    public Guardian createGuardian(Guardian guardian) {
        return guardianRepository.save(guardian);
    }

    @Override
    public List<Guardian> getAllGuardians() {
        return guardianRepository.findAll();
    }

    @Override
    public Optional<Guardian> getGuardianById(Long id) {
        return guardianRepository.findById(id);
    }

    @Override
    public Guardian updateGuardian(Long id, Guardian guardian) {
        Optional<Guardian> existingGuardian = guardianRepository.findById(id);
        if (existingGuardian.isPresent()) {
            Guardian updatedGuardian = existingGuardian.get();
            
            if (guardian.getRut() != null) {
                updatedGuardian.setRut(guardian.getRut());
            }
            if (guardian.getFirstName() != null) {
                updatedGuardian.setFirstName(guardian.getFirstName());
            }
            if (guardian.getLastName() != null) {
                updatedGuardian.setLastName(guardian.getLastName());
            }
            if (guardian.getSecondLastName() != null) {
                updatedGuardian.setSecondLastName(guardian.getSecondLastName());
            }
            if (guardian.getEmail() != null) {
                updatedGuardian.setEmail(guardian.getEmail());
            }
            if (guardian.getPhone() != null) {
                updatedGuardian.setPhone(guardian.getPhone());
            }
            if (guardian.getEmergencyPhone() != null) {
                updatedGuardian.setEmergencyPhone(guardian.getEmergencyPhone());
            }
            if (guardian.getAddress() != null) {
                updatedGuardian.setAddress(guardian.getAddress());
            }
            if (guardian.getCommune() != null) {
                updatedGuardian.setCommune(guardian.getCommune());
            }
            if (guardian.getCity() != null) {
                updatedGuardian.setCity(guardian.getCity());
            }
            if (guardian.getRelationship() != null) {
                updatedGuardian.setRelationship(guardian.getRelationship());
            }
            if (guardian.getOccupation() != null) {
                updatedGuardian.setOccupation(guardian.getOccupation());
            }
            if (guardian.getWorkplace() != null) {
                updatedGuardian.setWorkplace(guardian.getWorkplace());
            }
            if (guardian.getWorkPhone() != null) {
                updatedGuardian.setWorkPhone(guardian.getWorkPhone());
            }
            if (guardian.getIsPrimary() != null) {
                updatedGuardian.setIsPrimary(guardian.getIsPrimary());
            }
            if (guardian.getUserId() != null) {
                updatedGuardian.setUserId(guardian.getUserId());
            }
            
            return guardianRepository.save(updatedGuardian);
        }
        return null;
    }

    @Override
    public void deleteGuardian(Long id) {
        guardianRepository.deleteById(id);
    }
}
