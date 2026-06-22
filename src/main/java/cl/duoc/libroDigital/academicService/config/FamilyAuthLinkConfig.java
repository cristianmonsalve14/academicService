package cl.duoc.libroDigital.academicService.config;

import cl.duoc.libroDigital.academicService.model.Guardian;
import cl.duoc.libroDigital.academicService.model.Student;
import cl.duoc.libroDigital.academicService.repository.EnrollmentRepository;
import cl.duoc.libroDigital.academicService.repository.GuardianRepository;
import cl.duoc.libroDigital.academicService.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.List;

/**
 * Vincula cuentas demo de auth (por email) con registros académicos.
 * Usuarios: apoderado_demo / estudiante_demo (password test1234).
 */
@Configuration
public class FamilyAuthLinkConfig {

    private static final String DEMO_GUARDIAN_EMAIL = "apoderado@librodigital.cl";
    private static final String DEMO_STUDENT_EMAIL = "estudiante@librodigital.cl";

    @Bean
    public CommandLineRunner linkFamilyDemoAccounts(
            GuardianRepository guardianRepository,
            StudentRepository studentRepository,
            EnrollmentRepository enrollmentRepository) {
        return args -> {
            Student demoStudent = studentRepository.findByEmailIgnoreCase(DEMO_STUDENT_EMAIL)
                    .orElseGet(() -> pickDemoStudent(studentRepository, enrollmentRepository));

            if (demoStudent == null) {
                return;
            }

            if (demoStudent.getEmail() == null
                    || !DEMO_STUDENT_EMAIL.equalsIgnoreCase(demoStudent.getEmail().trim())) {
                demoStudent.setEmail(DEMO_STUDENT_EMAIL);
                studentRepository.save(demoStudent);
            }

            Guardian guardian = null;
            if (demoStudent.getGuardianId() != null) {
                guardian = guardianRepository.findById(demoStudent.getGuardianId()).orElse(null);
            }
            if (guardian == null) {
                guardian = guardianRepository.findByEmailIgnoreCase(DEMO_GUARDIAN_EMAIL).orElse(null);
            }
            if (guardian == null) {
                List<Guardian> guardians = guardianRepository.findAll();
                if (!guardians.isEmpty()) {
                    guardian = guardians.get(0);
                }
            }
            if (guardian == null) {
                return;
            }

            if (guardian.getEmail() == null
                    || !DEMO_GUARDIAN_EMAIL.equalsIgnoreCase(guardian.getEmail().trim())) {
                guardian.setEmail(DEMO_GUARDIAN_EMAIL);
                guardianRepository.save(guardian);
            }

            if (demoStudent.getGuardianId() == null || !demoStudent.getGuardianId().equals(guardian.getId())) {
                demoStudent.setGuardianId(guardian.getId());
                studentRepository.save(demoStudent);
            }
        };
    }

    private Student pickDemoStudent(
            StudentRepository studentRepository,
            EnrollmentRepository enrollmentRepository) {
        return enrollmentRepository.findAll().stream()
                .filter(enrollment -> enrollment.getStudentId() != null)
                .min(Comparator.comparing(e -> e.getStudentId()))
                .flatMap(enrollment -> studentRepository.findById(enrollment.getStudentId()))
                .orElseGet(() -> studentRepository.findAll().stream().findFirst().orElse(null));
    }
}
