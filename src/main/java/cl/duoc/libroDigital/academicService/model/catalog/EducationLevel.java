package cl.duoc.libroDigital.academicService.model.catalog;

import jakarta.persistence.*;

@Entity
@Table(name = "education_levels")
public class EducationLevel {
    @Id private Short id;
    @Column(nullable = false, unique = true) private String code;
    @Column(nullable = false) private String label;
    @Column(nullable = false) private String stage;
    public Short getId() { return id; }
    public String getCode() { return code; }
    public String getLabel() { return label; }
    public String getStage() { return stage; }
}
