package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.dto.GuardianDTO;
import cl.duoc.libroDigital.academicService.model.Guardian;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
import cl.duoc.libroDigital.academicService.service.GuardianService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/guardians")
public class GuardianController {

    @Autowired
    private GuardianService guardianService;

    @Autowired
    private CatalogLookupService catalogs;

    private GuardianDTO toDTO(Guardian guardian) {
        GuardianDTO dto = new GuardianDTO();
        dto.setId(guardian.getId());
        dto.setRut(guardian.getRut());
        dto.setFirstName(guardian.getFirstName());
        dto.setLastName(guardian.getLastName());
        dto.setSecondLastName(guardian.getSecondLastName());
        dto.setEmail(guardian.getEmail());
        dto.setPhone(guardian.getPhone());
        dto.setEmergencyPhone(guardian.getEmergencyPhone());
        dto.setAddress(guardian.getAddress());
        dto.setCommune(guardian.getCommune());
        dto.setCity(guardian.getCity());
        dto.setRelationship(catalogs.code("relationship_types", guardian.getRelationshipId()));
        dto.setOccupation(guardian.getOccupation());
        dto.setWorkplace(guardian.getWorkplace());
        dto.setWorkPhone(guardian.getWorkPhone());
        dto.setIsPrimary(guardian.getIsPrimary());
        dto.setUserId(guardian.getUserId());
        dto.setCreatedAt(guardian.getCreatedAt());
        dto.setUpdatedAt(guardian.getUpdatedAt());
        return dto;
    }

    private Guardian toEntity(GuardianDTO dto) {
        Guardian guardian = new Guardian();
        guardian.setId(dto.getId());
        guardian.setRut(dto.getRut());
        guardian.setFirstName(dto.getFirstName());
        guardian.setLastName(dto.getLastName());
        guardian.setSecondLastName(dto.getSecondLastName());
        guardian.setEmail(dto.getEmail());
        guardian.setPhone(dto.getPhone());
        guardian.setEmergencyPhone(dto.getEmergencyPhone());
        guardian.setAddress(dto.getAddress());
        guardian.setCommune(dto.getCommune());
        guardian.setCity(dto.getCity());
        guardian.setRelationshipId(catalogs.requireId("relationship_types", dto.getRelationship()));
        guardian.setOccupation(dto.getOccupation());
        guardian.setWorkplace(dto.getWorkplace());
        guardian.setWorkPhone(dto.getWorkPhone());
        guardian.setIsPrimary(dto.getIsPrimary());
        guardian.setUserId(dto.getUserId());
        return guardian;
    }

    @PostMapping
    public ResponseEntity<GuardianDTO> createGuardian(@RequestBody GuardianDTO dto) {
        return ResponseEntity.ok(toDTO(guardianService.createGuardian(toEntity(dto))));
    }

    @GetMapping
    public ResponseEntity<List<GuardianDTO>> getAllGuardians() {
        return ResponseEntity.ok(guardianService.getAllGuardians().stream().map(this::toDTO).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuardianDTO> getGuardianById(@PathVariable Long id) {
        return guardianService.getGuardianById(id)
                .map(g -> ResponseEntity.ok(toDTO(g)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuardianDTO> updateGuardian(@PathVariable Long id, @RequestBody GuardianDTO dto) {
        Guardian updated = guardianService.updateGuardian(id, toEntity(dto));
        if (updated != null) {
            return ResponseEntity.ok(toDTO(updated));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuardian(@PathVariable Long id) {
        guardianService.deleteGuardian(id);
        return ResponseEntity.noContent().build();
    }
}
