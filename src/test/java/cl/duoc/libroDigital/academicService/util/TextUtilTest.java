package cl.duoc.libroDigital.academicService.util;

import cl.duoc.libroDigital.academicService.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextUtilTest {

    @Test
    void requireNonBlank_returnsTrimmedValue() {
        assertEquals("valor", TextUtil.requireNonBlank("  valor  ", "El campo"));
    }

    @Test
    void requireNonBlank_blankThrows() {
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> TextUtil.requireNonBlank("  ", "El nombre"));
        assertEquals("El nombre es obligatorio", ex.getMessage());
    }

    @Test
    void trimToNull_blankReturnsNull() {
        assertNull(TextUtil.trimToNull(null));
        assertNull(TextUtil.trimToNull("   "));
    }

    @Test
    void trimToNull_returnsTrimmedValue() {
        assertEquals("texto", TextUtil.trimToNull("  texto "));
    }
}
