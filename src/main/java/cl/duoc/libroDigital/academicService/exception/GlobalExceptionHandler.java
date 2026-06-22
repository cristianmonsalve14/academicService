package cl.duoc.libroDigital.academicService.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, String>> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String detail = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();
        String message = "No se puede eliminar el registro porque tiene datos relacionados";
        if (detail != null) {
            if (detail.contains("fk_evaluations_subject")) {
                message = "No se puede eliminar la asignatura porque tiene evaluaciones asociadas. Elimine primero las evaluaciones de esta asignatura.";
            } else if (detail.contains("fk_courses_head_teacher") || detail.contains("teachers")) {
                message = "No se puede eliminar el profesor porque está asignado a cursos o asignaturas.";
            } else if (detail.contains("fk_grades") || detail.contains("grades")) {
                message = "No se puede eliminar porque existen notas asociadas.";
            } else if (detail.contains("fk_enrollments") || detail.contains("enrollments")) {
                message = "No se puede eliminar porque existen matrículas asociadas.";
            }
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", message));
    }
}
