package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.model.Guardian;
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

    // Crear apoderado
    @PostMapping
    public ResponseEntity<Guardian> createGuardian(@RequestBody Guardian guardian) {
        Guardian created = guardianService.createGuardian(guardian);
        return ResponseEntity.ok(created);
    }

    // Listar todos los apoderados
    @GetMapping
    public ResponseEntity<List<Guardian>> getAllGuardians() {
        List<Guardian> guardians = guardianService.getAllGuardians();
        return ResponseEntity.ok(guardians);
    }

    // Obtener apoderado por ID
    @GetMapping("/{id}")
    public ResponseEntity<Guardian> getGuardianById(@PathVariable Long id) {
        return guardianService.getGuardianById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Actualizar apoderado
    @PutMapping("/{id}")
    public ResponseEntity<Guardian> updateGuardian(@PathVariable Long id, @RequestBody Guardian guardian) {
        Guardian updated = guardianService.updateGuardian(id, guardian);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    // Eliminar apoderado
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuardian(@PathVariable Long id) {
        guardianService.deleteGuardian(id);
        return ResponseEntity.noContent().build();
    }
}
