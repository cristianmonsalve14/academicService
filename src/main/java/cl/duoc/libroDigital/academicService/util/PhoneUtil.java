package cl.duoc.libroDigital.academicService.util;

import cl.duoc.libroDigital.academicService.exception.BadRequestException;

public final class PhoneUtil {

    private PhoneUtil() {
    }

    public static String normalize(String phone) {
        if (phone == null) {
            return null;
        }
        String digits = phone.replaceAll("\\D", "");
        if (digits.startsWith("56") && digits.length() > 9) {
            digits = digits.substring(2);
        }
        return digits;
    }

    public static String normalizeAndValidateRequired(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new BadRequestException("El teléfono es obligatorio");
        }
        return normalizeAndValidateOptional(phone);
    }

    public static String normalizeAndValidateOptional(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        String digits = normalize(phone);
        if (digits.length() < 8 || digits.length() > 9) {
            throw new BadRequestException("El teléfono debe tener 8 o 9 dígitos (ej: 912345678)");
        }
        if (digits.length() == 9 && digits.charAt(0) != '9' && digits.charAt(0) != '2') {
            throw new BadRequestException("El teléfono móvil debe comenzar con 9 o fijo con 2");
        }
        return digits;
    }
}
