package cl.duoc.libroDigital.academicService.model.catalog;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "academic_years")
public class AcademicYear {
    @Id private Short id;
    @Column(nullable = false, unique = true) private Integer year;
    @Column(nullable = false, unique = true) private String code;
    @Column(name = "start_date", nullable = false) private LocalDate startDate;
    public Short getId() { return id; }
    public Integer getYear() { return year; }
    public String getCode() { return code; }
    public LocalDate getStartDate() { return startDate; }
}
