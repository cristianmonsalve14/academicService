package cl.duoc.libroDigital.academicService.model.catalog;

import jakarta.persistence.*;

@Entity
@Table(name = "enrollment_statuses")
public class EnrollmentStatus {
    @Id private Short id;
    @Column(nullable = false, unique = true) private String code;
    @Column(nullable = false) private String label;
    public Short getId() { return id; }
    public String getCode() { return code; }
    public String getLabel() { return label; }
}
