package cl.duoc.libroDigital.academicService.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "evaluations")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate date;

    private Long subjectId;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }
}
