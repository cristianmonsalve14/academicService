package cl.duoc.libroDigital.academicService.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleForbidden_returnsForbiddenStatus() {
        ResponseEntity<Map<String, String>> response =
                handler.handleForbidden(new ForbiddenException("No tiene permisos"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No tiene permisos", response.getBody().get("message"));
    }

    @Test
    void handleBadRequest_returnsBadRequestStatus() {
        ResponseEntity<Map<String, String>> response =
                handler.handleBadRequest(new BadRequestException("Datos inválidos"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Datos inválidos", response.getBody().get("message"));
    }

    @Test
    void handleConflict_returnsConflictStatus() {
        ResponseEntity<Map<String, String>> response =
                handler.handleConflict(new ConflictException("Registro duplicado"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Registro duplicado", response.getBody().get("message"));
    }

    @Test
    void handleNotFound_returnsNotFoundStatus() {
        ResponseEntity<Map<String, String>> response =
                handler.handleNotFound(new NotFoundException("No encontrado"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No encontrado", response.getBody().get("message"));
    }

    @Test
    void handleDataIntegrity_forSubjectFk_returnsSpecificMessage() {
        ResponseEntity<Map<String, String>> response =
                handler.handleDataIntegrity(dataIntegrity("fk_evaluations_subject constraint"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(
                "No se puede eliminar la asignatura porque tiene evaluaciones asociadas. Elimine primero las evaluaciones de esta asignatura.",
                response.getBody().get("message"));
    }

    @Test
    void handleDataIntegrity_forTeacherFk_returnsSpecificMessage() {
        ResponseEntity<Map<String, String>> response =
                handler.handleDataIntegrity(dataIntegrity("fk_courses_head_teacher constraint"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(
                "No se puede eliminar el profesor porque está asignado a cursos o asignaturas.",
                response.getBody().get("message"));
    }

    @Test
    void handleDataIntegrity_forGradesFk_returnsSpecificMessage() {
        ResponseEntity<Map<String, String>> response =
                handler.handleDataIntegrity(dataIntegrity("fk_grades constraint"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("No se puede eliminar porque existen notas asociadas.", response.getBody().get("message"));
    }

    @Test
    void handleDataIntegrity_forEnrollmentsFk_returnsSpecificMessage() {
        ResponseEntity<Map<String, String>> response =
                handler.handleDataIntegrity(dataIntegrity("fk_enrollments constraint"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("No se puede eliminar porque existen matrículas asociadas.", response.getBody().get("message"));
    }

    @Test
    void handleDataIntegrity_defaultBranch_returnsGenericMessage() {
        ResponseEntity<Map<String, String>> response =
                handler.handleDataIntegrity(dataIntegrity("otra restricción"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(
                "No se puede eliminar el registro porque tiene datos relacionados",
                response.getBody().get("message"));
    }

    private static DataIntegrityViolationException dataIntegrity(String detail) {
        return new DataIntegrityViolationException("integridad", new RuntimeException(detail));
    }
}
