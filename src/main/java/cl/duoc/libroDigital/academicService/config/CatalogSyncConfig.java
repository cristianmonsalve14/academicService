package cl.duoc.libroDigital.academicService.config;

import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class CatalogSyncConfig {

    @Bean
    public CommandLineRunner syncShiftCatalog(ShiftCatalogSync shiftCatalogSync) {
        return args -> shiftCatalogSync.syncShifts();
    }

    @Component
    static class ShiftCatalogSync {

        @PersistenceContext
        private EntityManager em;

        private final CatalogLookupService catalogs;

        ShiftCatalogSync(CatalogLookupService catalogs) {
            this.catalogs = catalogs;
        }

        @Transactional
        public void syncShifts() {
            em.createNativeQuery("""
                    UPDATE shifts
                    SET code = 'COMPLETA', label = 'Jornada completa'
                    WHERE code = 'VESPERTINO'
                    """)
                    .executeUpdate();
            catalogs.loadCatalogs();
        }
    }
}
