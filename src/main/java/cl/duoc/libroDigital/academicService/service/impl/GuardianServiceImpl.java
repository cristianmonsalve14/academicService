package cl.duoc.libroDigital.academicService.service.impl;



import cl.duoc.libroDigital.academicService.exception.NotFoundException;

import cl.duoc.libroDigital.academicService.model.Guardian;

import cl.duoc.libroDigital.academicService.repository.GuardianRepository;

import cl.duoc.libroDigital.academicService.service.GuardianService;

import cl.duoc.libroDigital.academicService.validation.AcademicEntityValidator;

import org.springframework.stereotype.Service;



import java.util.List;

import java.util.Optional;



@Service

public class GuardianServiceImpl implements GuardianService {

    private final GuardianRepository guardianRepository;
    private final AcademicEntityValidator validator;

    public GuardianServiceImpl(GuardianRepository guardianRepository, AcademicEntityValidator validator) {
        this.guardianRepository = guardianRepository;
        this.validator = validator;
    }

    @Override

    public Guardian createGuardian(Guardian guardian) {

        validator.validateGuardianForSave(guardian, null);

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

        return guardianRepository.findById(id).map(updatedGuardian -> {

            if (guardian.getRut() != null) updatedGuardian.setRut(guardian.getRut());

            if (guardian.getFirstName() != null) updatedGuardian.setFirstName(guardian.getFirstName());

            if (guardian.getLastName() != null) updatedGuardian.setLastName(guardian.getLastName());

            if (guardian.getSecondLastName() != null) updatedGuardian.setSecondLastName(guardian.getSecondLastName());

            if (guardian.getEmail() != null) updatedGuardian.setEmail(guardian.getEmail());

            if (guardian.getPhone() != null) updatedGuardian.setPhone(guardian.getPhone());

            if (guardian.getEmergencyPhone() != null) updatedGuardian.setEmergencyPhone(guardian.getEmergencyPhone());

            if (guardian.getAddress() != null) updatedGuardian.setAddress(guardian.getAddress());

            if (guardian.getCommune() != null) updatedGuardian.setCommune(guardian.getCommune());

            if (guardian.getCity() != null) updatedGuardian.setCity(guardian.getCity());

            if (guardian.getRelationshipId() != null) updatedGuardian.setRelationshipId(guardian.getRelationshipId());

            if (guardian.getOccupation() != null) updatedGuardian.setOccupation(guardian.getOccupation());

            if (guardian.getWorkplace() != null) updatedGuardian.setWorkplace(guardian.getWorkplace());

            if (guardian.getWorkPhone() != null) updatedGuardian.setWorkPhone(guardian.getWorkPhone());

            if (guardian.getIsPrimary() != null) updatedGuardian.setIsPrimary(guardian.getIsPrimary());

            if (guardian.getUserId() != null) updatedGuardian.setUserId(guardian.getUserId());



            validator.validateGuardianForSave(updatedGuardian, id);

            return guardianRepository.save(updatedGuardian);

        }).orElseThrow(() -> new NotFoundException("Apoderado no encontrado con id " + id));

    }



    @Override

    public void deleteGuardian(Long id) {

        guardianRepository.deleteById(id);

    }

}

