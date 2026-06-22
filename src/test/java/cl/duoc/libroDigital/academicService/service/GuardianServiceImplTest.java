package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.exception.NotFoundException;
import cl.duoc.libroDigital.academicService.model.Guardian;
import cl.duoc.libroDigital.academicService.repository.GuardianRepository;
import cl.duoc.libroDigital.academicService.service.impl.GuardianServiceImpl;
import cl.duoc.libroDigital.academicService.validation.AcademicEntityValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuardianServiceImplTest {

    @Mock
    private GuardianRepository guardianRepository;

    @Mock
    private AcademicEntityValidator validator;

    @InjectMocks
    private GuardianServiceImpl guardianService;

    @Test
    void createGuardian_validatesAndSaves() {
        Guardian guardian = new Guardian();
        guardian.setRut("11.111.111-1");
        guardian.setFirstName("María");
        guardian.setLastName("González");
        when(guardianRepository.save(guardian)).thenReturn(guardian);

        Guardian saved = guardianService.createGuardian(guardian);

        assertSame(guardian, saved);
        verify(validator).validateGuardianForSave(guardian, null);
        verify(guardianRepository).save(guardian);
    }

    @Test
    void getGuardianById_returnsGuardian() {
        Guardian guardian = new Guardian();
        guardian.setId(1L);
        guardian.setFirstName("Pedro");
        when(guardianRepository.findById(1L)).thenReturn(Optional.of(guardian));

        Optional<Guardian> result = guardianService.getGuardianById(1L);

        assertTrue(result.isPresent());
        assertEquals("Pedro", result.get().getFirstName());
    }

    @Test
    void updateGuardian_appliesChangesAndSaves() {
        Guardian existing = new Guardian();
        existing.setId(3L);
        existing.setPhone("+56911111111");

        Guardian update = new Guardian();
        update.setPhone("+56922222222");
        update.setEmail("apoderado@example.com");

        when(guardianRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(guardianRepository.save(existing)).thenReturn(existing);

        Guardian result = guardianService.updateGuardian(3L, update);

        assertEquals("+56922222222", result.getPhone());
        assertEquals("apoderado@example.com", result.getEmail());
        verify(validator).validateGuardianForSave(existing, 3L);
        verify(guardianRepository).save(existing);
    }

    @Test
    void updateGuardian_notFoundThrows() {
        when(guardianRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> guardianService.updateGuardian(99L, new Guardian()));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void deleteGuardian_deletesById() {
        guardianService.deleteGuardian(7L);
        verify(guardianRepository).deleteById(7L);
    }
}
