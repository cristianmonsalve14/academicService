package cl.duoc.libroDigital.academicService.util;

import cl.duoc.libroDigital.academicService.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RutUtilTest {

    @Test
    void normalizeAndValidate_validRut() {
        assertEquals("12.345.678-5", RutUtil.normalizeAndValidate("12345678-5"));
        assertEquals("12.345.678-5", RutUtil.normalizeAndValidate("12.345.678-5"));
    }

    @Test
    void normalizeAndValidate_invalidRut() {
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> RutUtil.normalizeAndValidate("12345678-0"));
        assertEquals("El RUT no es válido", ex.getMessage());
    }

    @Test
    void normalizeAndValidate_blankRut() {
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> RutUtil.normalizeAndValidate("   "));
        assertEquals("El RUT es obligatorio", ex.getMessage());
    }

    @Test
    void normalizeAndValidate_nullRut() {
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> RutUtil.normalizeAndValidate(null));
        assertEquals("El RUT es obligatorio", ex.getMessage());
    }
}
