package cl.duoc.libroDigital.academicService.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CatalogLookupServiceTest {

    @Mock
    private EntityManager entityManager;

    private CatalogLookupService catalogs;

    @BeforeEach
    void setUp() throws Exception {
        catalogs = new CatalogLookupService();
        setField("em", entityManager);

        putCatalog("teacher_statuses", Map.of("ACTIVO", (short) 1));
        putCatalog("academic_years", Map.of("2026", (short) 9));
        putCatalog("shifts", Map.of(
                "MATUTINO", (short) 1,
                "VESPERTINO", (short) 7));
    }

    @Test
    void requireId_returnsMappedCatalogValue() {
        assertEquals((short) 1, catalogs.requireId("teacher_statuses", "ACTIVO"));
    }

    @Test
    void requireId_usesDefaultWhenCodeIsBlank() {
        assertEquals((short) 1, catalogs.requireId("teacher_statuses", " "));
    }

    @Test
    void requireId_shiftAliasCompletaMapsToVespertino() {
        assertEquals((short) 7, catalogs.requireId("shifts", "COMPLETA"));
    }

    @Test
    void code_shiftIdVespertinoIsExposedAsCompleta() {
        assertEquals("COMPLETA", catalogs.code("shifts", (short) 7));
    }

    @Test
    void requireId_unknownAcademicYearFallsBackToDefault() {
        assertEquals((short) 2, catalogs.requireId("academic_years", "2035"));
    }

    @Test
    void academicYearIdFromDate_usesYearCodeLookup() {
        assertEquals((short) 9, catalogs.academicYearIdFromDate(LocalDate.of(2026, 5, 20)));
    }

    @Test
    void entityFor_unknownTableThrowsIllegalArgumentException() throws Exception {
        Method entityFor = CatalogLookupService.class.getDeclaredMethod("entityFor", String.class);
        entityFor.setAccessible(true);

        InvocationTargetException ex = assertThrows(
                InvocationTargetException.class,
                () -> entityFor.invoke(catalogs, "unknown_table"));

        assertEquals(IllegalArgumentException.class, ex.getCause().getClass());
    }

    @SuppressWarnings("unchecked")
    private void putCatalog(String table, Map<String, Short> codes) throws Exception {
        Map<String, Map<String, Short>> codeToId =
                (Map<String, Map<String, Short>>) getField("codeToId").get(catalogs);
        Map<String, Map<Short, String>> idToCode =
                (Map<String, Map<Short, String>>) getField("idToCode").get(catalogs);

        Map<String, Short> normalizedCodes = new HashMap<>();
        Map<Short, String> reverse = new HashMap<>();
        for (Map.Entry<String, Short> entry : codes.entrySet()) {
            String key = entry.getKey().toUpperCase();
            normalizedCodes.put(key, entry.getValue());
            reverse.put(entry.getValue(), key);
        }
        codeToId.put(table, normalizedCodes);
        idToCode.put(table, reverse);
    }

    private void setField(String fieldName, Object value) throws Exception {
        Field field = getField(fieldName);
        field.set(catalogs, value);
    }

    private Field getField(String name) throws Exception {
        Field field = CatalogLookupService.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
}
