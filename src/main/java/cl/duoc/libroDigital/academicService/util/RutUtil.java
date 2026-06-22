package cl.duoc.libroDigital.academicService.util;

import cl.duoc.libroDigital.academicService.exception.BadRequestException;

public final class RutUtil {

    private RutUtil() {
    }

    public static String normalizeAndValidate(String rut) {
        if (rut == null || rut.isBlank()) {
            throw new BadRequestException("El RUT es obligatorio");
        }

        String cleaned = rut.replaceAll("[^0-9kK]", "").toUpperCase();
        if (cleaned.length() < 2) {
            throw new BadRequestException("El RUT no es válido");
        }

        String body = cleaned.substring(0, cleaned.length() - 1);
        char dv = cleaned.charAt(cleaned.length() - 1);

        if (!body.matches("\\d+")) {
            throw new BadRequestException("El RUT no es válido");
        }

        int sum = 0;
        int multiplier = 2;
        for (int i = body.length() - 1; i >= 0; i--) {
            sum += Character.getNumericValue(body.charAt(i)) * multiplier;
            multiplier = multiplier == 7 ? 2 : multiplier + 1;
        }

        int mod = 11 - (sum % 11);
        char expected = mod == 11 ? '0' : mod == 10 ? 'K' : Character.forDigit(mod, 10);
        if (dv != expected) {
            throw new BadRequestException("El RUT no es válido");
        }

        return format(body, dv);
    }

    private static String format(String body, char dv) {
        StringBuilder reversed = new StringBuilder(body).reverse();
        StringBuilder withDots = new StringBuilder();
        for (int i = 0; i < reversed.length(); i++) {
            if (i > 0 && i % 3 == 0) {
                withDots.append('.');
            }
            withDots.append(reversed.charAt(i));
        }
        return withDots.reverse().append('-').append(dv).toString();
    }
}
