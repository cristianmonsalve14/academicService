package cl.duoc.libroDigital.academicService.config;

import cl.duoc.libroDigital.academicService.repository.TeacherRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TeacherAuthLinkConfig {

    @Bean
    public CommandLineRunner linkDemoTeacher(TeacherRepository teacherRepository) {
        return args -> {
            if (teacherRepository.findByAuthUsername("prof_castillo").isPresent()) {
                return;
            }

            teacherRepository.findByEmail("prof.castillo@duoc.cl").ifPresentOrElse(teacher -> {
                teacher.setAuthUsername("prof_castillo");
                teacherRepository.save(teacher);
            }, () -> teacherRepository.findAll().stream().findFirst().ifPresent(teacher -> {
                if (teacher.getAuthUsername() == null || teacher.getAuthUsername().isBlank()) {
                    teacher.setAuthUsername("prof_castillo");
                    teacherRepository.save(teacher);
                }
            }));
        };
    }
}
