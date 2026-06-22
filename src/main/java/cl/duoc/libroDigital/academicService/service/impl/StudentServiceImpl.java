package cl.duoc.libroDigital.academicService.service.impl;



import cl.duoc.libroDigital.academicService.exception.NotFoundException;

import cl.duoc.libroDigital.academicService.model.Student;

import cl.duoc.libroDigital.academicService.repository.StudentRepository;

import cl.duoc.libroDigital.academicService.service.CatalogLookupService;

import cl.duoc.libroDigital.academicService.service.StudentService;

import cl.duoc.libroDigital.academicService.validation.AcademicEntityValidator;

import org.springframework.stereotype.Service;



import java.util.List;

import java.util.Optional;



@Service

public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final CatalogLookupService catalogs;
    private final AcademicEntityValidator validator;

    public StudentServiceImpl(
            StudentRepository studentRepository,
            CatalogLookupService catalogs,
            AcademicEntityValidator validator) {
        this.studentRepository = studentRepository;
        this.catalogs = catalogs;
        this.validator = validator;
    }

    @Override

    public Student createStudent(Student student) {

        student.setEnrollmentNumber(null);

        validator.validateStudentForSave(student, null);

        return studentRepository.save(student);

    }



    @Override

    public List<Student> getAllStudents() {

        return studentRepository.findAll();

    }



    @Override

    public Optional<Student> getStudentById(Long id) {

        return studentRepository.findById(id);

    }



    @Override

    public Student updateStudent(Long id, Student student) {

        return studentRepository.findById(id).map(existingStudent -> {

            if (student.getRut() != null) existingStudent.setRut(student.getRut());

            if (student.getFirstName() != null) existingStudent.setFirstName(student.getFirstName());

            if (student.getSecondName() != null) existingStudent.setSecondName(student.getSecondName());

            if (student.getLastName() != null) existingStudent.setLastName(student.getLastName());

            if (student.getMotherLastName() != null) existingStudent.setMotherLastName(student.getMotherLastName());

            if (student.getDateOfBirth() != null) existingStudent.setDateOfBirth(student.getDateOfBirth());

            if (student.getPhone() != null) existingStudent.setPhone(student.getPhone());

            if (student.getEmail() != null) existingStudent.setEmail(student.getEmail());

            if (student.getAddress() != null) existingStudent.setAddress(student.getAddress());

            if (student.getCommune() != null) existingStudent.setCommune(student.getCommune());

            if (student.getCity() != null) existingStudent.setCity(student.getCity());

            if (student.getStudentStatusId() != null) existingStudent.setStudentStatusId(student.getStudentStatusId());

            if (student.getAdmissionDate() != null) existingStudent.setAdmissionDate(student.getAdmissionDate());

            if (student.getWithdrawalDate() != null) existingStudent.setWithdrawalDate(student.getWithdrawalDate());

            if (student.getGuardianId() != null) existingStudent.setGuardianId(student.getGuardianId());

            if (student.getUserId() != null) existingStudent.setUserId(student.getUserId());



            validator.validateStudentForSave(existingStudent, id);

            return studentRepository.save(existingStudent);

        }).orElseThrow(() -> new NotFoundException("Estudiante no encontrado con id " + id));

    }



    @Override

    public void deleteStudent(Long id) {

        studentRepository.deleteById(id);

    }



    @Override

    public Optional<Student> getStudentByRut(String rut) {

        return studentRepository.findByRut(rut);

    }



    @Override

    public Optional<Student> getStudentByEmail(String email) {

        return studentRepository.findByEmail(email);

    }



    @Override

    public List<Student> getStudentsByStatus(String studentStatus) {

        return studentRepository.findByStudentStatusId(catalogs.requireId("student_statuses", studentStatus));

    }

}

