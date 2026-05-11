package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.model.Student;
import cl.duoc.libroDigital.academicService.service.StudentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    //crear estudiante
    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    //listar estudiantes
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    //obtener por id
    @GetMapping("/{id}")
    public Student getStudent(@PathVariable Long id) {
        return studentService.getStudentById(id).orElse(null);
    }

    //actualizar estudiante
    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody Student student) {
        return studentService.updateStudent(id, student);
    }

    //eliminar estudiante
    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }
}
