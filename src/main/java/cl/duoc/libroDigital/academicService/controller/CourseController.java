package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.model.Course;
import cl.duoc.libroDigital.academicService.service.CourseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // crear curso
    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        return courseService.createCourse(course);
    }

    // listar cursos
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

        // actualizar curso
    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Long id, @RequestBody Course course) {
        return courseService.updateCourse(id, course);
    }

     // eliminar curso
    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }

    // obtener por id
    @GetMapping("/{id}")
    public Course getCourse(@PathVariable Long id) {
        return courseService.getCourseById(id).orElse(null);
    }
}
