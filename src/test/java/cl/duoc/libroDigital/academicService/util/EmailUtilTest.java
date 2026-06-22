package cl.duoc.libroDigital.academicService.util;

import cl.duoc.libroDigital.academicService.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailUtilTest {

    @Test
    void normalizeAndValidate_validEmail() {
        assertEquals("usuario@duoc.cl", EmailUtil.normalizeAndValidate("  Usuario@Duoc.CL "));
    }

    @Test
    void normalizeAndValidate_invalidEmail() {
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> EmailUtil.normalizeAndValidate("no-es-email"));
        assertEquals("El email no tiene un formato válido", ex.getMessage());
    }

    @Test
    void normalizeAndValidate_blankEmail() {
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> EmailUtil.normalizeAndValidate(""));
        assertEquals("El email es obligatorio", ex.getMessage());
    }

    @Test
    void normalizeOptional_blankReturnsNull() {
        assertNull(EmailUtil.normalizeOptional(null));
        assertNull(EmailUtil.normalizeOptional("  "));
    }

    @Test
    void normalizeOptional_validEmail() {
        assertEquals("contacto@duoc.cl", EmailUtil.normalizeOptional("contacto@duoc.cl"));
    }
}
