package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.model.Enrollment;
import cl.duoc.libroDigital.academicService.model.Student;
import cl.duoc.libroDigital.academicService.repository.StudentRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EnrollmentNumberService {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("^(\\d{4})-(\\d+)$");

    private final StudentRepository studentRepository;
    private final CatalogLookupService catalogs;

    public EnrollmentNumberService(StudentRepository studentRepository, CatalogLookupService catalogs) {
        this.studentRepository = studentRepository;
        this.catalogs = catalogs;
    }

    public void assignIfNeeded(Enrollment enrollment) {
        if (enrollment.getStudentId() == null) {
            return;
        }

        Student student = studentRepository.findById(enrollment.getStudentId()).orElse(null);
        if (student == null) {
            return;
        }

        int year = resolveAcademicYear(enrollment);
        String yearPrefix = year + "-";
        String current = student.getEnrollmentNumber();

        if (current != null && !current.isBlank() && current.startsWith(yearPrefix)) {
            return;
        }

        student.setEnrollmentNumber(generateNextForYear(year));
        studentRepository.save(student);
    }

    public int resolveAcademicYear(Enrollment enrollment) {
        Integer academicYear = catalogs.academicYearValue(enrollment.getAcademicYearId());
        if (academicYear != null) {
            return academicYear;
        }
        LocalDate enrollmentDate = enrollment.getEnrollmentDate();
        if (enrollmentDate != null) {
            return enrollmentDate.getYear();
        }
        return LocalDate.now().getYear();
    }

    public String generateNextForYear(int year) {
        String prefix = year + "-";
        int maxSequence = studentRepository.findAll().stream()
                .map(Student::getEnrollmentNumber)
                .map(this::parseSequence)
                .filter(seq -> seq != null && seq.year() == year)
                .mapToInt(SequenceParts::sequence)
                .max()
                .orElse(0);
        return prefix + (maxSequence + 1);
    }

    private SequenceParts parseSequence(String enrollmentNumber) {
        if (enrollmentNumber == null || enrollmentNumber.isBlank()) {
            return null;
        }
        Matcher matcher = NUMBER_PATTERN.matcher(enrollmentNumber.trim());
        if (!matcher.matches()) {
            return null;
        }
        return new SequenceParts(
                Integer.parseInt(matcher.group(1)),
                Integer.parseInt(matcher.group(2)));
    }

    private record SequenceParts(int year, int sequence) {}
}
