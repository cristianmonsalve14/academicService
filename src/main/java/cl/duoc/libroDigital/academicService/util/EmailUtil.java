package cl.duoc.libroDigital.academicService.util;

import cl.duoc.libroDigital.academicService.exception.BadRequestException;

public final class EmailUtil {

    private static final String EMAIL_PATTERN = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";

    private EmailUtil() {
    }

    public static String normalizeAndValidate(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("El email es obligatorio");
        }
        String normalized = email.trim().toLowerCase();
        if (!normalized.matches(EMAIL_PATTERN)) {
            throw new BadRequestException("El email no tiene un formato válido");
        }
        return normalized;
    }

    public static String normalizeOptional(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return normalizeAndValidate(email);
    }
}
