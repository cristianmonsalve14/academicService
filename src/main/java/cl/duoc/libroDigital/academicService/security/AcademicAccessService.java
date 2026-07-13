package cl.duoc.libroDigital.academicService.security;



import cl.duoc.libroDigital.academicService.exception.ForbiddenException;

import cl.duoc.libroDigital.academicService.model.Enrollment;

import cl.duoc.libroDigital.academicService.model.Evaluation;

import cl.duoc.libroDigital.academicService.model.Grade;

import cl.duoc.libroDigital.academicService.model.Guardian;

import cl.duoc.libroDigital.academicService.model.Student;

import cl.duoc.libroDigital.academicService.model.Subject;

import cl.duoc.libroDigital.academicService.model.Teacher;

import cl.duoc.libroDigital.academicService.repository.CourseRepository;

import cl.duoc.libroDigital.academicService.repository.EnrollmentRepository;

import cl.duoc.libroDigital.academicService.repository.EvaluationRepository;

import cl.duoc.libroDigital.academicService.repository.GradeRepository;

import cl.duoc.libroDigital.academicService.repository.GuardianRepository;

import cl.duoc.libroDigital.academicService.repository.StudentRepository;

import cl.duoc.libroDigital.academicService.repository.SubjectRepository;

import cl.duoc.libroDigital.academicService.repository.TeacherRepository;



import org.springframework.security.core.Authentication;

import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;



import java.util.HashSet;

import java.util.List;

import java.util.Objects;

import java.util.Optional;

import java.util.Set;

import java.util.stream.Collectors;



@Service

public class AcademicAccessService {



    private final TeacherRepository teacherRepository;

    private final StudentRepository studentRepository;

    private final GuardianRepository guardianRepository;

    private final SubjectRepository subjectRepository;

    private final CourseRepository courseRepository;

    private final EnrollmentRepository enrollmentRepository;

    private final EvaluationRepository evaluationRepository;

    private final GradeRepository gradeRepository;



    public AcademicAccessService(

            TeacherRepository teacherRepository,

            StudentRepository studentRepository,

            GuardianRepository guardianRepository,

            SubjectRepository subjectRepository,

            CourseRepository courseRepository,

            EnrollmentRepository enrollmentRepository,

            EvaluationRepository evaluationRepository,

            GradeRepository gradeRepository) {

        this.teacherRepository = teacherRepository;

        this.studentRepository = studentRepository;

        this.guardianRepository = guardianRepository;

        this.subjectRepository = subjectRepository;

        this.courseRepository = courseRepository;

        this.enrollmentRepository = enrollmentRepository;

        this.evaluationRepository = evaluationRepository;

        this.gradeRepository = gradeRepository;

    }



    public boolean isAdmin() {
        return hasRole("ADMINISTRADOR")
                || hasRole("SUPER_ADMINISTRADOR")
                || hasRole("ADMINISTRATIVO");
    }

    public boolean isSuperAdmin() {
        return hasRole("ADMINISTRADOR") || hasRole("SUPER_ADMINISTRADOR");
    }

    public boolean isTeacher() {

        return hasRole("DOCENTE");

    }



    public boolean isGuardian() {

        return hasRole("APODERADO");

    }



    public boolean isStudent() {

        return hasRole("ESTUDIANTE");

    }



    public void requireAdmin() {
        if (!isAdmin()) {
            throw new ForbiddenException("Solo administración puede realizar esta acción");
        }
    }

    public void requireSuperAdmin() {
        if (!isSuperAdmin()) {
            throw new ForbiddenException("Solo super administración puede realizar esta acción");
        }
    }

    public void requireTeacherOrAdmin() {

        if (!isAdmin() && !isTeacher()) {

            throw new ForbiddenException("No tiene permisos para esta acción");

        }

    }



    public void requireFamilyOrStaffRead() {

        if (!isAdmin() && !isTeacher() && !isGuardian() && !isStudent()) {

            throw new ForbiddenException("No tiene permisos para esta acción");

        }

    }



    /** Notas, evaluaciones y registros pedagógicos: solo docentes pueden escribir. */

    public void requireTeacherForPedagogicalWrite() {

        if (isAdmin()) {

            throw new ForbiddenException("La administración solo puede consultar esta información, no modificarla");

        }

        if (!isTeacher()) {

            throw new ForbiddenException("No tiene permisos para esta acción");

        }

    }



    public Optional<Long> currentTeacherId() {

        return currentTeacher().map(Teacher::getId);

    }



    public Long requireTeacherId() {

        return currentTeacherId()

                .orElseThrow(() -> new ForbiddenException("No hay un profesor vinculado a esta cuenta"));

    }



    public Optional<Long> currentStudentId() {

        return currentStudent().map(Student::getId);

    }



    public Long requireStudentId() {

        return currentStudentId()

                .orElseThrow(() -> new ForbiddenException("No hay un estudiante vinculado a esta cuenta"));

    }



    public Optional<Long> currentGuardianId() {

        return currentGuardian().map(Guardian::getId);

    }



    public Long requireGuardianId() {

        return currentGuardianId()

                .orElseThrow(() -> new ForbiddenException("No hay un apoderado vinculado a esta cuenta"));

    }



    public Optional<Teacher> currentTeacher() {

        if (!isTeacher()) {

            return Optional.empty();

        }

        return currentPrincipal().flatMap(this::resolveTeacher);

    }



    public Optional<Student> currentStudent() {

        if (!isStudent()) {

            return Optional.empty();

        }

        return currentPrincipal().flatMap(this::resolveStudent);

    }



    public Optional<Guardian> currentGuardian() {

        if (!isGuardian()) {

            return Optional.empty();

        }

        return currentPrincipal().flatMap(this::resolveGuardian);

    }



    public List<Long> guardianStudentIds(Long guardianId) {

        return studentRepository.findByGuardianId(guardianId).stream()

                .map(Student::getId)

                .toList();

    }



    public Set<Long> familyAccessibleStudentIds() {

        if (isStudent()) {

            return currentStudentId().map(Set::of).orElse(Set.of());

        }

        if (isGuardian()) {

            return new HashSet<>(guardianStudentIds(requireGuardianId()));

        }

        return Set.of();

    }



    public List<Long> teacherSubjectIds(Long teacherId) {

        return subjectRepository.findByTeacherId(teacherId).stream()

                .map(Subject::getId)

                .toList();

    }



    public Set<Long> teacherCourseIds(Long teacherId) {

        Set<Long> courseIds = new HashSet<>();

        subjectRepository.findByTeacherId(teacherId).stream()

                .map(Subject::getCourseId)

                .filter(Objects::nonNull)

                .forEach(courseIds::add);

        courseRepository.findAll().stream()

                .filter(course -> teacherId.equals(course.getHeadTeacherId()))

                .map(course -> course.getId())

                .forEach(courseIds::add);

        return courseIds;

    }



    public Set<Long> teacherStudentIds(Long teacherId) {

        Set<Long> courseIds = teacherCourseIds(teacherId);

        if (courseIds.isEmpty()) {

            return Set.of();

        }

        return enrollmentRepository.findAll().stream()

                .filter(enrollment -> courseIds.contains(enrollment.getCourseId()))

                .map(Enrollment::getStudentId)

                .collect(Collectors.toSet());

    }



    public Set<Long> studentCourseIds(Long studentId) {

        return enrollmentRepository.findByStudentId(studentId).stream()

                .map(Enrollment::getCourseId)

                .filter(Objects::nonNull)

                .collect(Collectors.toSet());

    }



    public Set<Long> guardianCourseIds(Long guardianId) {

        Set<Long> wardIds = new HashSet<>(guardianStudentIds(guardianId));

        return enrollmentRepository.findAll().stream()

                .filter(enrollment -> wardIds.contains(enrollment.getStudentId()))

                .map(Enrollment::getCourseId)

                .filter(Objects::nonNull)

                .collect(Collectors.toSet());

    }



    public void ensureCanReadSubject(Long subjectId) {

        if (isAdmin()) {

            return;

        }

        Subject subject = subjectRepository.findById(subjectId)

                .orElseThrow(() -> new ForbiddenException("Asignatura no encontrada"));

        if (isTeacher()) {

            Long teacherId = requireTeacherId();

            if (!teacherId.equals(subject.getTeacherId())) {

                throw new ForbiddenException("No puede acceder a esta asignatura");

            }

            return;

        }

        if (isStudent()) {

            Long selfId = requireStudentId();

            if (!studentCourseIds(selfId).contains(subject.getCourseId())) {

                throw new ForbiddenException("No puede acceder a esta asignatura");

            }

            return;

        }

        if (isGuardian()) {

            Long guardianId = requireGuardianId();

            if (!guardianCourseIds(guardianId).contains(subject.getCourseId())) {

                throw new ForbiddenException("No puede acceder a esta asignatura");

            }

            return;

        }

        throw new ForbiddenException("No puede acceder a esta asignatura");

    }



    public void ensureCanManageSubject(Long subjectId) {

        requireTeacherForPedagogicalWrite();

        ensureCanReadSubject(subjectId);

    }



    public void ensureCanReadEvaluation(Long evaluationId) {

        if (isAdmin()) {

            return;

        }

        Evaluation evaluation = evaluationRepository.findById(evaluationId)

                .orElseThrow(() -> new ForbiddenException("Evaluación no encontrada"));

        if (isTeacher()) {

            ensureCanReadSubject(evaluation.getSubjectId());

            return;

        }

        throw new ForbiddenException("No puede acceder a esta evaluación");

    }



    public void ensureCanManageEvaluation(Long evaluationId) {

        requireTeacherForPedagogicalWrite();

        Evaluation evaluation = evaluationRepository.findById(evaluationId)

                .orElseThrow(() -> new ForbiddenException("Evaluación no encontrada"));

        ensureCanReadSubject(evaluation.getSubjectId());

    }



    public void ensureCanManageEvaluationSubject(Long subjectId) {

        requireTeacherForPedagogicalWrite();

        ensureCanReadSubject(subjectId);

    }



    public void ensureCanReadGrade(Long gradeId) {

        if (isAdmin()) {

            return;

        }

        Grade grade = gradeRepository.findById(gradeId)

                .orElseThrow(() -> new ForbiddenException("Nota no encontrada"));

        if (isTeacher()) {

            ensureCanReadEvaluation(grade.getEvaluationId());

            return;

        }

        if (isStudent() || isGuardian()) {

            ensureCanReadStudent(grade.getStudentId());

            return;

        }

        throw new ForbiddenException("No puede acceder a esta nota");

    }



    public void ensureCanManageGrade(Long gradeId) {

        requireTeacherForPedagogicalWrite();

        Grade grade = gradeRepository.findById(gradeId)

                .orElseThrow(() -> new ForbiddenException("Nota no encontrada"));

        ensureCanReadEvaluation(grade.getEvaluationId());

    }



    public void ensureCanManageGradeForEvaluation(Long evaluationId) {

        requireTeacherForPedagogicalWrite();

        ensureCanReadEvaluation(evaluationId);

    }



    public void ensureCanReadStudent(Long studentId) {

        if (isAdmin()) {

            return;

        }

        if (isStudent()) {

            Long selfId = requireStudentId();

            if (!selfId.equals(studentId)) {

                throw new ForbiddenException("Solo puede consultar su propia información");

            }

            return;

        }

        if (isGuardian()) {

            Long guardianId = requireGuardianId();

            if (!guardianStudentIds(guardianId).contains(studentId)) {

                throw new ForbiddenException("No puede acceder a este estudiante");

            }

            return;

        }

        Long teacherId = requireTeacherId();

        if (!teacherStudentIds(teacherId).contains(studentId)) {

            throw new ForbiddenException("No puede acceder a este estudiante");

        }

    }



    public void ensureCanReadCourse(Long courseId) {

        if (isAdmin()) {

            return;

        }

        if (isStudent()) {

            Long studentId = requireStudentId();

            if (!studentCourseIds(studentId).contains(courseId)) {

                throw new ForbiddenException("No puede acceder a este curso");

            }

            return;

        }

        if (isGuardian()) {

            Long guardianId = requireGuardianId();

            boolean hasWardInCourse = enrollmentRepository.findAll().stream()

                    .anyMatch(enrollment -> courseId.equals(enrollment.getCourseId())

                            && guardianStudentIds(guardianId).contains(enrollment.getStudentId()));

            if (!hasWardInCourse) {

                throw new ForbiddenException("No puede acceder a este curso");

            }

            return;

        }

        Long teacherId = requireTeacherId();

        if (!teacherCourseIds(teacherId).contains(courseId)) {

            throw new ForbiddenException("No puede acceder a este curso");

        }

    }



    public void ensureCanReadTeacher(Long teacherId) {

        if (isAdmin()) {

            return;

        }

        Long currentId = requireTeacherId();

        if (!currentId.equals(teacherId)) {

            throw new ForbiddenException("Solo puede ver su propio perfil docente");

        }

    }



    private Optional<JwtUserPrincipal> currentPrincipal() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {

            return Optional.empty();

        }

        if (auth.getPrincipal() instanceof JwtUserPrincipal principal) {

            return Optional.of(principal);

        }

        return Optional.empty();

    }



    private Optional<Teacher> resolveTeacher(JwtUserPrincipal principal) {

        if (principal.getUserId() != null) {

            Optional<Teacher> byUserId = teacherRepository.findByUserId(principal.getUserId());

            if (byUserId.isPresent()) {

                return byUserId;

            }

        }

        if (principal.getUsername() != null && !principal.getUsername().isBlank()) {

            Optional<Teacher> byUsername = teacherRepository.findByAuthUsername(principal.getUsername());

            if (byUsername.isPresent()) {

                return Optional.of(linkTeacherAccess(byUsername.get(), principal));

            }

        }

        if (principal.getEmail() != null && !principal.getEmail().isBlank()) {

            Optional<Teacher> byEmail = teacherRepository.findByEmailIgnoreCase(principal.getEmail().trim());

            if (byEmail.isPresent()) {

                return Optional.of(linkTeacherAccess(byEmail.get(), principal));

            }

        }

        return Optional.empty();

    }

    /** Vincula ficha docente con la cuenta de acceso la primera vez que coincide. */
    private Teacher linkTeacherAccess(Teacher teacher, JwtUserPrincipal principal) {
        boolean changed = false;
        if (principal.getUserId() != null && !Objects.equals(teacher.getUserId(), principal.getUserId())) {
            teacher.setUserId(principal.getUserId());
            changed = true;
        }
        if (principal.getUsername() != null && !principal.getUsername().isBlank()
                && (teacher.getAuthUsername() == null || teacher.getAuthUsername().isBlank()
                || !teacher.getAuthUsername().equalsIgnoreCase(principal.getUsername()))) {
            // Solo sobrescribe si estaba vacío o coincide en mayúsculas/minúsculas
            if (teacher.getAuthUsername() == null || teacher.getAuthUsername().isBlank()) {
                teacher.setAuthUsername(principal.getUsername());
                changed = true;
            }
        }
        return changed ? teacherRepository.save(teacher) : teacher;
    }



    private Optional<Student> resolveStudent(JwtUserPrincipal principal) {

        if (principal.getUserId() != null) {

            Optional<Student> byUserId = studentRepository.findByUserId(principal.getUserId());

            if (byUserId.isPresent()) {

                return byUserId;

            }

        }

        if (principal.getEmail() != null && !principal.getEmail().isBlank()) {

            return studentRepository.findByEmailIgnoreCase(principal.getEmail().trim());

        }

        return Optional.empty();

    }



    private Optional<Guardian> resolveGuardian(JwtUserPrincipal principal) {

        if (principal.getUserId() != null) {

            Optional<Guardian> byUserId = guardianRepository.findByUserId(principal.getUserId());

            if (byUserId.isPresent()) {

                return byUserId;

            }

        }

        if (principal.getEmail() != null && !principal.getEmail().isBlank()) {

            return guardianRepository.findByEmailIgnoreCase(principal.getEmail().trim());

        }

        return Optional.empty();

    }



    private boolean hasRole(String role) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {

            return false;

        }

        String roleAuthority = "ROLE_" + role;

        return auth.getAuthorities().stream()

                .map(GrantedAuthority::getAuthority)

                .anyMatch(authority -> authority.equals(roleAuthority) || authority.equals(role));

    }

}


