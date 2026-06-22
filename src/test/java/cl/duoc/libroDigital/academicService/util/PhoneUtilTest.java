package cl.duoc.libroDigital.academicService.util;

import cl.duoc.libroDigital.academicService.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhoneUtilTest {

    @Test
    void normalize_stripsFormattingAndCountryCode() {
        assertEquals("912345678", PhoneUtil.normalize("+56 9 1234 5678"));
        assertNull(PhoneUtil.normalize(null));
    }

    @Test
    void normalizeAndValidateRequired_validMobile() {
        assertEquals("912345678", PhoneUtil.normalizeAndValidateRequired("9 1234 5678"));
    }

    @Test
    void normalizeAndValidateRequired_blankThrows() {
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> PhoneUtil.normalizeAndValidateRequired(" "));
        assertEquals("El teléfono es obligatorio", ex.getMessage());
    }

    @Test
    void normalizeAndValidateOptional_blankReturnsNull() {
        assertNull(PhoneUtil.normalizeAndValidateOptional(null));
        assertNull(PhoneUtil.normalizeAndValidateOptional("   "));
    }

    @Test
    void normalizeAndValidateOptional_invalidLengthThrows() {
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> PhoneUtil.normalizeAndValidateOptional("12345"));
        assertEquals("El teléfono debe tener 8 o 9 dígitos (ej: 912345678)", ex.getMessage());
    }
}
