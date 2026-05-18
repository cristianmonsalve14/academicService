package cl.duoc.libroDigital.academicService.service.impl;

import cl.duoc.libroDigital.academicService.model.Teacher;
import cl.duoc.libroDigital.academicService.repository.TeacherRepository;
import cl.duoc.libroDigital.academicService.service.TeacherService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    public Teacher createTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @Override
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    @Override
    public Optional<Teacher> getTeacherById(Long id) {
        return teacherRepository.findById(id);
    }

    @Override
    public Teacher updateTeacher(Long id, Teacher teacher) {

        return teacherRepository.findById(id).map(existing -> {

            if (teacher.getRut() != null) {
                existing.setRut(teacher.getRut());
            }

            if (teacher.getFirstName() != null) {
                existing.setFirstName(teacher.getFirstName());
            }

            if (teacher.getLastName() != null) {
                existing.setLastName(teacher.getLastName());
            }

            if (teacher.getSecondLastName() != null) {
                existing.setSecondLastName(teacher.getSecondLastName());
            }

            if (teacher.getEmail() != null) {
                existing.setEmail(teacher.getEmail());
            }

            if (teacher.getPhone() != null) {
                existing.setPhone(teacher.getPhone());
            }

            if (teacher.getAddress() != null) {
                existing.setAddress(teacher.getAddress());
            }

            if (teacher.getCommune() != null) {
                existing.setCommune(teacher.getCommune());
            }

            if (teacher.getCity() != null) {
                existing.setCity(teacher.getCity());
            }

            if (teacher.getEmployeeNumber() != null) {
                existing.setEmployeeNumber(teacher.getEmployeeNumber());
            }

            if (teacher.getSpecialization() != null) {
                existing.setSpecialization(teacher.getSpecialization());
            }

            if (teacher.getEducationLevel() != null) {
                existing.setEducationLevel(teacher.getEducationLevel());
            }

            if (teacher.getHireDate() != null) {
                existing.setHireDate(teacher.getHireDate());
            }

            if (teacher.getContractType() != null) {
                existing.setContractType(teacher.getContractType());
            }

            if (teacher.getTeacherStatus() != null) {
                existing.setTeacherStatus(teacher.getTeacherStatus());
            }

            return teacherRepository.save(existing);

        }).orElseThrow(() ->
                new RuntimeException("Teacher no encontrado con id " + id)
        );
    }

    @Override
    public void deleteTeacher(Long id) {
        teacherRepository.deleteById(id);
    }

    // ===== MÉTODOS EXTRA =====

    @Override
    public Optional<Teacher> getTeacherByRut(String rut) {
        return teacherRepository.findByRut(rut);
    }

    @Override
    public Optional<Teacher> getTeacherByEmail(String email) {
        return teacherRepository.findByEmail(email);
    }

    @Override
    public List<Teacher> getTeachersByStatus(String teacherStatus) {
        return teacherRepository.findByTeacherStatus(teacherStatus);
    }
}