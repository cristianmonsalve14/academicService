package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.model.Teacher;
import cl.duoc.libroDigital.academicService.dto.TeacherDTO;
import cl.duoc.libroDigital.academicService.service.TeacherService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    // ===== Mapper: Entity -> DTO =====
    private TeacherDTO toDTO(Teacher teacher) {
        TeacherDTO dto = new TeacherDTO();

        dto.setId(teacher.getId());
        dto.setRut(teacher.getRut());
        dto.setFirstName(teacher.getFirstName());
        dto.setLastName(teacher.getLastName());
        dto.setSecondLastName(teacher.getSecondLastName());

        dto.setEmail(teacher.getEmail());
        dto.setPhone(teacher.getPhone());
        dto.setAddress(teacher.getAddress());
        dto.setCommune(teacher.getCommune());
        dto.setCity(teacher.getCity());

        dto.setEmployeeNumber(teacher.getEmployeeNumber());
        dto.setSpecialization(teacher.getSpecialization());
        dto.setEducationLevel(teacher.getEducationLevel());
        dto.setHireDate(teacher.getHireDate());
        dto.setContractType(teacher.getContractType());
        dto.setTeacherStatus(teacher.getTeacherStatus());

        dto.setCreatedAt(teacher.getCreatedAt());
        dto.setUpdatedAt(teacher.getUpdatedAt());

        return dto;
    }

    // ===== Mapper: DTO -> Entity =====
    private Teacher toEntity(TeacherDTO dto) {
        Teacher teacher = new Teacher();

        teacher.setId(dto.getId());
        teacher.setRut(dto.getRut());
        teacher.setFirstName(dto.getFirstName());
        teacher.setLastName(dto.getLastName());
        teacher.setSecondLastName(dto.getSecondLastName());

        teacher.setEmail(dto.getEmail());
        teacher.setPhone(dto.getPhone());
        teacher.setAddress(dto.getAddress());
        teacher.setCommune(dto.getCommune());
        teacher.setCity(dto.getCity());

        teacher.setEmployeeNumber(dto.getEmployeeNumber());
        teacher.setSpecialization(dto.getSpecialization());
        teacher.setEducationLevel(dto.getEducationLevel());
        teacher.setHireDate(dto.getHireDate());
        teacher.setContractType(dto.getContractType());
        teacher.setTeacherStatus(dto.getTeacherStatus());

        return teacher;
    }

    // ===== Crear =====
    @PostMapping
    public TeacherDTO createTeacher(@RequestBody TeacherDTO dto) {
        Teacher created = teacherService.createTeacher(toEntity(dto));
        return toDTO(created);
    }

    // ===== Listar =====
    @GetMapping
    public List<TeacherDTO> getAllTeachers() {
        return teacherService.getAllTeachers()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ===== Obtener por ID =====
    @GetMapping("/{id}")
    public TeacherDTO getTeacherById(@PathVariable Long id) {
        return teacherService.getTeacherById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    // ===== Actualizar =====
    @PutMapping("/{id}")
    public TeacherDTO updateTeacher(@PathVariable Long id, @RequestBody TeacherDTO dto) {
        Teacher updated = teacherService.updateTeacher(id, toEntity(dto));
        return toDTO(updated);
    }

    // ===== Eliminar =====
    @DeleteMapping("/{id}")
    public void deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
    }
}