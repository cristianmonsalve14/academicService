package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    Student createStudent(Student student);

    List<Student> getAllStudents();

    Optional<Student> getStudentById(Long id);
}
