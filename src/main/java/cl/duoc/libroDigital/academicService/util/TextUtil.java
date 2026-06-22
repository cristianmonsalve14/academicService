package cl.duoc.libroDigital.academicService.util;

import cl.duoc.libroDigital.academicService.exception.BadRequestException;

public final class TextUtil {

    private TextUtil() {
    }

    public static String requireNonBlank(String value, String fieldLabel) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException(fieldLabel + " es obligatorio");
        }
        return value.trim();
    }

    public static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
