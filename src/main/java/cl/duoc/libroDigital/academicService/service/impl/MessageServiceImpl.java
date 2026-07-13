package cl.duoc.libroDigital.academicService.service.impl;

import cl.duoc.libroDigital.academicService.dto.AttendanceNotifyRequest;
import cl.duoc.libroDigital.academicService.dto.ConversationDTO;
import cl.duoc.libroDigital.academicService.dto.CreateConversationRequest;
import cl.duoc.libroDigital.academicService.dto.MessageContactDTO;
import cl.duoc.libroDigital.academicService.dto.MessageDTO;
import cl.duoc.libroDigital.academicService.dto.SendMessageRequest;
import cl.duoc.libroDigital.academicService.dto.UnreadCountDTO;
import cl.duoc.libroDigital.academicService.exception.BadRequestException;
import cl.duoc.libroDigital.academicService.exception.ForbiddenException;
import cl.duoc.libroDigital.academicService.exception.NotFoundException;
import cl.duoc.libroDigital.academicService.model.Conversation;
import cl.duoc.libroDigital.academicService.model.ConversationReadState;
import cl.duoc.libroDigital.academicService.model.Enrollment;
import cl.duoc.libroDigital.academicService.model.Evaluation;
import cl.duoc.libroDigital.academicService.model.Message;
import cl.duoc.libroDigital.academicService.model.Student;
import cl.duoc.libroDigital.academicService.model.Subject;
import cl.duoc.libroDigital.academicService.repository.ConversationReadStateRepository;
import cl.duoc.libroDigital.academicService.repository.ConversationRepository;
import cl.duoc.libroDigital.academicService.repository.CourseRepository;
import cl.duoc.libroDigital.academicService.repository.EnrollmentRepository;
import cl.duoc.libroDigital.academicService.repository.GuardianRepository;
import cl.duoc.libroDigital.academicService.repository.MessageRepository;
import cl.duoc.libroDigital.academicService.repository.StudentRepository;
import cl.duoc.libroDigital.academicService.repository.SubjectRepository;
import cl.duoc.libroDigital.academicService.repository.TeacherRepository;
import cl.duoc.libroDigital.academicService.security.AcademicAccessService;
import cl.duoc.libroDigital.academicService.security.JwtUserPrincipal;
import cl.duoc.libroDigital.academicService.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);

    public static final String TYPE_ADMIN_TEACHER = "AD";
    public static final String TYPE_ADMIN_GUARDIAN = "AG";
    public static final String TYPE_ADMIN_STUDENT = "AS";
    public static final String TYPE_TEACHER_GUARDIAN = "TG";
    public static final String TYPE_TEACHER_STUDENT = "TS";

    private static final Set<String> OFFICE_TYPES = Set.of(
            TYPE_ADMIN_TEACHER, TYPE_ADMIN_GUARDIAN, TYPE_ADMIN_STUDENT);

    private static final Set<String> ALLOWED_TYPES = Set.of(
            TYPE_ADMIN_TEACHER, TYPE_ADMIN_GUARDIAN, TYPE_ADMIN_STUDENT,
            TYPE_TEACHER_GUARDIAN, TYPE_TEACHER_STUDENT);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ConversationReadStateRepository readStateRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final GuardianRepository guardianRepository;
    private final SubjectRepository subjectRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AcademicAccessService access;

    public MessageServiceImpl(
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            ConversationReadStateRepository readStateRepository,
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            GuardianRepository guardianRepository,
            SubjectRepository subjectRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            AcademicAccessService access) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.readStateRepository = readStateRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.guardianRepository = guardianRepository;
        this.subjectRepository = subjectRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.access = access;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationDTO> listMyConversations() {
        List<Conversation> conversations;
        if (access.isAdmin()) {
            conversations = conversationRepository.findByConversationTypeInOrderByUpdatedAtDesc(OFFICE_TYPES);
        } else if (access.isTeacher()) {
            conversations = conversationRepository.findByTeacherIdOrderByUpdatedAtDesc(access.requireTeacherId());
        } else if (access.isGuardian()) {
            conversations = conversationRepository.findByGuardianIdOrderByUpdatedAtDesc(access.requireGuardianId())
                    .stream()
                    .filter(c -> !Objects.equals(c.getConversationType(), "GS"))
                    .collect(Collectors.toList());
        } else if (access.isStudent()) {
            conversations = conversationRepository.findByStudentIdOrderByUpdatedAtDesc(access.requireStudentId())
                    .stream()
                    .filter(c -> !Objects.equals(c.getConversationType(), "GS"))
                    .collect(Collectors.toList());
        } else {
            throw new ForbiddenException("No tiene acceso a mensajería");
        }

        SenderContext me = currentSender();
        if (conversations.isEmpty()) {
            return List.of();
        }

        List<Long> conversationIds = conversations.stream().map(Conversation::getId).collect(Collectors.toList());
        Map<Long, List<Message>> messagesByConversation = messageRepository.findByConversationIdIn(conversationIds)
                .stream()
                .collect(Collectors.groupingBy(Message::getConversationId));
        Map<Long, LocalDateTime> lastReadByConversation = new HashMap<>();
        if (me.userId != null) {
            for (ConversationReadState state : readStateRepository.findByUserIdAndConversationIdIn(me.userId, conversationIds)) {
                lastReadByConversation.put(state.getConversationId(), state.getLastReadAt());
            }
        }

        return conversations.stream()
                .map(c -> toConversationDto(c, me, lastReadByConversation, messagesByConversation))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UnreadCountDTO countUnreadMessages() {
        long total = listMyConversations().stream().mapToLong(c -> c.unreadCount).sum();
        return new UnreadCountDTO(total);
    }

    @Override
    @Transactional
    public ConversationDTO openOrGetConversation(CreateConversationRequest request) {
        String type = normalizeType(request.getConversationType());
        Long studentId = request.getStudentId();
        Long teacherId = request.getTeacherId();
        Long guardianId = request.getGuardianId();

        validateAndAuthorizeOpen(type, studentId, teacherId, guardianId);

        String threadKey = buildThreadKey(type, studentId, teacherId, guardianId);
        Conversation conversation = conversationRepository.findByThreadKey(threadKey)
                .orElseGet(() -> {
                    Conversation created = new Conversation();
                    created.setConversationType(type);
                    created.setThreadKey(threadKey);
                    created.setStudentId(resolveStudentId(type, studentId));
                    created.setTeacherId(resolveTeacherId(type, teacherId));
                    created.setGuardianId(resolveGuardianId(type, guardianId));
                    return conversationRepository.save(created);
                });
        return toConversationDto(conversation);
    }

    @Override
    @Transactional
    public List<MessageDTO> listMessages(Long conversationId) {
        Conversation conversation = getAccessibleConversation(conversationId);
        SenderContext me = currentSender();
        List<MessageDTO> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId()).stream()
                .map(message -> toMessageDto(message, me))
                .collect(Collectors.toList());
        markConversationRead(conversation.getId(), me);
        return messages;
    }

    @Override
    @Transactional
    public MessageDTO sendMessage(Long conversationId, SendMessageRequest request) {
        String body = requireMessageBody(request);

        Conversation conversation = getAccessibleConversation(conversationId);
        SenderContext me = currentSender();
        ensureSenderBelongs(conversation, me);

        Message message = new Message();
        message.setConversationId(conversation.getId());
        message.setSenderRole(me.role);
        message.setSenderUserId(me.userId);
        message.setSenderProfileId(me.profileId);
        message.setBody(body);
        Message saved = messageRepository.save(message);

        conversation.setUpdatedAt(java.time.LocalDateTime.now());
        conversationRepository.save(conversation);
        markConversationRead(conversation.getId(), me);
        return toMessageDto(saved, me);
    }

    @Override
    @Transactional
    public MessageDTO updateMessage(Long conversationId, Long messageId, SendMessageRequest request) {
        String body = requireMessageBody(request);
        Conversation conversation = getAccessibleConversation(conversationId);
        SenderContext me = currentSender();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Mensaje no encontrado"));
        if (!Objects.equals(message.getConversationId(), conversation.getId())) {
            throw new NotFoundException("Mensaje no encontrado en esta conversación");
        }
        ensureMessageAuthor(message, me);
        message.setBody(body);
        Message saved = messageRepository.save(message);
        conversation.setUpdatedAt(java.time.LocalDateTime.now());
        conversationRepository.save(conversation);
        return toMessageDto(saved, me);
    }

    @Override
    @Transactional
    public void deleteMessage(Long conversationId, Long messageId) {
        Conversation conversation = getAccessibleConversation(conversationId);
        SenderContext me = currentSender();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Mensaje no encontrado"));
        if (!Objects.equals(message.getConversationId(), conversation.getId())) {
            throw new NotFoundException("Mensaje no encontrado en esta conversación");
        }
        ensureMessageAuthor(message, me);
        messageRepository.delete(message);
        conversation.setUpdatedAt(java.time.LocalDateTime.now());
        conversationRepository.save(conversation);
    }

    @Override
    @Transactional
    public void deleteConversation(Long conversationId) {
        Conversation conversation = getAccessibleConversation(conversationId);
        messageRepository.deleteByConversationId(conversation.getId());
        readStateRepository.deleteByConversationId(conversation.getId());
        conversationRepository.delete(conversation);
    }

    @Override
    @Transactional
    public MessageDTO notifyGuardianAttendance(AttendanceNotifyRequest request) {
        if (request == null || request.getStudentId() == null) {
            throw new BadRequestException("studentId es obligatorio");
        }
        if (request.getStatus() == null || request.getStatus().isBlank()) {
            throw new BadRequestException("El estado de asistencia es obligatorio");
        }

        Long teacherId = access.requireTeacherId();
        Long studentId = request.getStudentId();
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado"));
        if (student.getGuardianId() == null) {
            return null;
        }
        if (!access.teacherStudentIds(teacherId).contains(studentId)) {
            throw new ForbiddenException("El docente no tiene vínculo académico con el estudiante");
        }

        CreateConversationRequest open = new CreateConversationRequest();
        open.setConversationType(TYPE_TEACHER_GUARDIAN);
        open.setStudentId(studentId);
        open.setTeacherId(teacherId);
        open.setGuardianId(student.getGuardianId());
        ConversationDTO conversation = openOrGetConversation(open);

        String studentName = request.getStudentName();
        if (studentName == null || studentName.isBlank()) {
            studentName = fullName(student.getFirstName(), student.getLastName());
        }
        String statusLabel = attendanceStatusLabel(request.getStatus());
        String subject = blankToDash(request.getSubjectName());
        String date = blankToDash(request.getSessionDate());

        String body = "Aviso de asistencia\n"
                + "Alumno: " + studentName + "\n"
                + "Asignatura: " + subject + "\n"
                + "Fecha de la clase: " + date + "\n"
                + "Estado: " + statusLabel + "\n\n"
                + "Este mensaje se generó automáticamente al registrar la asistencia de la clase.";

        SendMessageRequest send = new SendMessageRequest();
        send.setBody(body);
        return sendMessage(conversation.getId(), send);
    }

    @Override
    @Transactional
    public int notifyGuardiansEvaluationCreated(Evaluation evaluation, String evaluationTypeLabel) {
        if (evaluation == null || evaluation.getSubjectId() == null) {
            return 0;
        }
        // Solo cuando el docente crea la evaluación (contexto JWT de docente).
        if (!access.isTeacher()) {
            return 0;
        }
        Long teacherId = access.requireTeacherId();

        Subject subject = subjectRepository.findById(evaluation.getSubjectId()).orElse(null);
        if (subject == null || subject.getCourseId() == null) {
            return 0;
        }

        String subjectName = blankToDash(subject.getSubjectName());
        String courseName = courseRepository.findById(subject.getCourseId())
                .map(c -> blankToDash(c.getName()))
                .orElse("—");
        String evalName = blankToDash(evaluation.getName());
        String typeLabel = blankToDash(evaluationTypeLabel);
        String dateLabel = evaluation.getDate() != null
                ? evaluation.getDate().format(DATE_FMT)
                : "—";

        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(subject.getCourseId());
        int sent = 0;
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getStudentId() == null) {
                continue;
            }
            try {
                Student student = studentRepository.findById(enrollment.getStudentId()).orElse(null);
                if (student == null || student.getGuardianId() == null) {
                    continue;
                }
                if (!access.teacherStudentIds(teacherId).contains(student.getId())) {
                    continue;
                }

                CreateConversationRequest open = new CreateConversationRequest();
                open.setConversationType(TYPE_TEACHER_GUARDIAN);
                open.setStudentId(student.getId());
                open.setTeacherId(teacherId);
                open.setGuardianId(student.getGuardianId());
                ConversationDTO conversation = openOrGetConversation(open);

                String studentName = fullName(student.getFirstName(), student.getLastName());
                String body = "Aviso de evaluación\n"
                        + "Alumno: " + blankToDash(studentName) + "\n"
                        + "Curso: " + courseName + "\n"
                        + "Asignatura: " + subjectName + "\n"
                        + "Evaluación: " + evalName + "\n"
                        + "Tipo: " + typeLabel + "\n"
                        + "Fecha: " + dateLabel + "\n\n"
                        + "Este mensaje se generó automáticamente al publicar una nueva evaluación.";

                SendMessageRequest send = new SendMessageRequest();
                send.setBody(body);
                sendMessage(conversation.getId(), send);
                sent++;
            } catch (Exception ex) {
                log.warn("No se pudo avisar al apoderado del alumno {}: {}",
                        enrollment.getStudentId(), ex.getMessage());
            }
        }
        return sent;
    }

    private static String attendanceStatusLabel(String status) {
        if (status == null) {
            return "Sin marcar";
        }
        return switch (status.trim().toUpperCase()) {
            case "PRESENTE" -> "Presente (P)";
            case "AUSENTE" -> "Ausente (A)";
            case "ATRASADO" -> "Atrasado (R)";
            case "JUSTIFICADO" -> "Justificado (J)";
            default -> status.trim();
        };
    }

    private static String blankToDash(String value) {
        return value == null || value.isBlank() ? "—" : value.trim();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageContactDTO> listContacts() {
        if (access.isAdmin()) {
            return contactsForOffice();
        }
        if (access.isTeacher()) {
            return contactsForTeacher(access.requireTeacherId());
        }
        if (access.isGuardian()) {
            return contactsForGuardian(access.requireGuardianId());
        }
        if (access.isStudent()) {
            return contactsForStudent(access.requireStudentId());
        }
        throw new ForbiddenException("No tiene acceso a mensajería");
    }

    private List<MessageContactDTO> contactsForOffice() {
        List<MessageContactDTO> contacts = new ArrayList<>();
        teacherRepository.findAll().forEach(teacher -> {
            MessageContactDTO dto = baseContact(TYPE_ADMIN_TEACHER, null, null, teacher.getId(), null);
            dto.setTeacherName(fullName(teacher.getFirstName(), teacher.getLastName()));
            dto.setLabel("Docente: " + dto.getTeacherName());
            contacts.add(dto);
        });
        guardianRepository.findAll().forEach(guardian -> {
            MessageContactDTO dto = baseContact(TYPE_ADMIN_GUARDIAN, null, null, null, guardian.getId());
            dto.setGuardianName(fullName(guardian.getFirstName(), guardian.getLastName()));
            dto.setLabel("Apoderado: " + dto.getGuardianName());
            contacts.add(dto);
        });
        studentRepository.findAll().forEach(student -> {
            MessageContactDTO dto = baseContact(
                    TYPE_ADMIN_STUDENT, student.getId(), fullName(student.getFirstName(), student.getLastName()), null, null);
            dto.setLabel("Alumno: " + dto.getStudentName());
            contacts.add(dto);
        });
        return contacts;
    }

    private List<MessageContactDTO> contactsForTeacher(Long teacherId) {
        List<MessageContactDTO> contacts = new ArrayList<>();

        MessageContactDTO office = baseContact(TYPE_ADMIN_TEACHER, null, null, teacherId, null);
        office.setLabel("Oficina / Coordinación");
        contacts.add(office);

        Set<Long> studentIds = access.teacherStudentIds(teacherId);
        for (Long studentId : studentIds) {
            Student student = studentRepository.findById(studentId).orElse(null);
            if (student == null) continue;
            String studentName = fullName(student.getFirstName(), student.getLastName());

            MessageContactDTO withStudent = baseContact(TYPE_TEACHER_STUDENT, studentId, studentName, teacherId, null);
            withStudent.setLabel("Alumno: " + studentName);
            contacts.add(withStudent);

            if (student.getGuardianId() != null) {
                guardianRepository.findById(student.getGuardianId()).ifPresent(guardian -> {
                    MessageContactDTO withGuardian = baseContact(
                            TYPE_TEACHER_GUARDIAN, studentId, studentName, teacherId, guardian.getId());
                    withGuardian.setGuardianName(fullName(guardian.getFirstName(), guardian.getLastName()));
                    withGuardian.setLabel("Apoderado de " + studentName + ": " + withGuardian.getGuardianName());
                    contacts.add(withGuardian);
                });
            }
        }
        return contacts;
    }

    private List<MessageContactDTO> contactsForGuardian(Long guardianId) {
        List<MessageContactDTO> contacts = new ArrayList<>();

        MessageContactDTO office = baseContact(TYPE_ADMIN_GUARDIAN, null, null, null, guardianId);
        office.setLabel("Oficina / Coordinación");
        contacts.add(office);

        List<Student> wards = studentRepository.findByGuardianId(guardianId);
        for (Student student : wards) {
            String studentName = fullName(student.getFirstName(), student.getLastName());
            for (Long teacherId : studentTeacherIds(student.getId())) {
                teacherRepository.findById(teacherId).ifPresent(teacher -> {
                    MessageContactDTO withTeacher = baseContact(
                            TYPE_TEACHER_GUARDIAN, student.getId(), studentName, teacherId, guardianId);
                    withTeacher.setTeacherName(fullName(teacher.getFirstName(), teacher.getLastName()));
                    withTeacher.setLabel("Docente de " + studentName + ": " + withTeacher.getTeacherName());
                    contacts.add(withTeacher);
                });
            }
        }
        return contacts;
    }

    private List<MessageContactDTO> contactsForStudent(Long studentId) {
        List<MessageContactDTO> contacts = new ArrayList<>();
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado"));
        String studentName = fullName(student.getFirstName(), student.getLastName());

        MessageContactDTO office = baseContact(TYPE_ADMIN_STUDENT, studentId, studentName, null, null);
        office.setLabel("Oficina / Coordinación");
        contacts.add(office);

        for (Long teacherId : studentTeacherIds(studentId)) {
            teacherRepository.findById(teacherId).ifPresent(teacher -> {
                MessageContactDTO withTeacher = baseContact(
                        TYPE_TEACHER_STUDENT, studentId, studentName, teacherId, null);
                withTeacher.setTeacherName(fullName(teacher.getFirstName(), teacher.getLastName()));
                withTeacher.setLabel("Docente: " + withTeacher.getTeacherName());
                contacts.add(withTeacher);
            });
        }
        return contacts;
    }

    private Set<Long> studentTeacherIds(Long studentId) {
        Set<Long> courseIds = access.studentCourseIds(studentId);
        Set<Long> teacherIds = new HashSet<>();
        if (courseIds.isEmpty()) {
            return teacherIds;
        }
        for (Subject subject : subjectRepository.findAll()) {
            if (subject.getCourseId() != null
                    && courseIds.contains(subject.getCourseId())
                    && subject.getTeacherId() != null) {
                teacherIds.add(subject.getTeacherId());
            }
        }
        courseRepository.findAll().stream()
                .filter(course -> courseIds.contains(course.getId()))
                .map(course -> course.getHeadTeacherId())
                .filter(Objects::nonNull)
                .forEach(teacherIds::add);
        return teacherIds;
    }

    private void validateAndAuthorizeOpen(String type, Long studentId, Long teacherId, Long guardianId) {
        switch (type) {
            case TYPE_ADMIN_TEACHER -> {
                Long tId = requirePositive(teacherId, "teacherId");
                teacherRepository.findById(tId)
                        .orElseThrow(() -> new NotFoundException("Docente no encontrado"));
                assertOfficeOrTeacher(tId);
            }
            case TYPE_ADMIN_GUARDIAN -> {
                Long gId = requirePositive(guardianId, "guardianId");
                guardianRepository.findById(gId)
                        .orElseThrow(() -> new NotFoundException("Apoderado no encontrado"));
                assertOfficeOrGuardian(gId);
            }
            case TYPE_ADMIN_STUDENT -> {
                Long sId = requirePositive(studentId, "studentId");
                studentRepository.findById(sId)
                        .orElseThrow(() -> new NotFoundException("Estudiante no encontrado"));
                assertOfficeOrStudent(sId);
            }
            case TYPE_TEACHER_GUARDIAN -> {
                Long sId = requirePositive(studentId, "studentId");
                Long tId = requirePositive(teacherId, "teacherId");
                Long gId = requirePositive(guardianId, "guardianId");
                Student student = studentRepository.findById(sId)
                        .orElseThrow(() -> new NotFoundException("Estudiante no encontrado"));
                if (student.getGuardianId() == null || !student.getGuardianId().equals(gId)) {
                    throw new BadRequestException("El apoderado no corresponde al estudiante");
                }
                if (!access.teacherStudentIds(tId).contains(sId)) {
                    throw new ForbiddenException("El docente no tiene vínculo académico con el estudiante");
                }
                assertTeacherOrGuardian(tId, gId);
            }
            case TYPE_TEACHER_STUDENT -> {
                Long sId = requirePositive(studentId, "studentId");
                Long tId = requirePositive(teacherId, "teacherId");
                if (!access.teacherStudentIds(tId).contains(sId)) {
                    throw new ForbiddenException("El docente no tiene vínculo académico con el estudiante");
                }
                assertTeacherOrStudent(tId, sId);
            }
            default -> throw new BadRequestException("Tipo de conversación no válido");
        }
    }

    private void assertOfficeOrTeacher(Long teacherId) {
        if (access.isAdmin()) return;
        if (access.isTeacher() && access.requireTeacherId().equals(teacherId)) return;
        throw new ForbiddenException("No puedes abrir esta conversación");
    }

    private void assertOfficeOrGuardian(Long guardianId) {
        if (access.isAdmin()) return;
        if (access.isGuardian() && access.requireGuardianId().equals(guardianId)) return;
        throw new ForbiddenException("No puedes abrir esta conversación");
    }

    private void assertOfficeOrStudent(Long studentId) {
        if (access.isAdmin()) return;
        if (access.isStudent() && access.requireStudentId().equals(studentId)) return;
        throw new ForbiddenException("No puedes abrir esta conversación");
    }

    private void assertTeacherOrGuardian(Long teacherId, Long guardianId) {
        if (access.isTeacher() && access.requireTeacherId().equals(teacherId)) return;
        if (access.isGuardian() && access.requireGuardianId().equals(guardianId)) return;
        throw new ForbiddenException("No puedes abrir esta conversación");
    }

    private void assertTeacherOrStudent(Long teacherId, Long studentId) {
        if (access.isTeacher() && access.requireTeacherId().equals(teacherId)) return;
        if (access.isStudent() && access.requireStudentId().equals(studentId)) return;
        throw new ForbiddenException("No puedes abrir esta conversación");
    }

    private Conversation getAccessibleConversation(Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new NotFoundException("Conversación no encontrada"));
        ensureSenderBelongs(conversation, currentSender());
        return conversation;
    }

    private void ensureSenderBelongs(Conversation conversation, SenderContext me) {
        boolean ok = switch (me.role) {
            case "ADMINISTRATIVO", "SUPER_ADMINISTRADOR", "ADMINISTRADOR" ->
                    OFFICE_TYPES.contains(conversation.getConversationType());
            case "DOCENTE" -> Objects.equals(conversation.getTeacherId(), me.profileId);
            case "APODERADO" -> Objects.equals(conversation.getGuardianId(), me.profileId)
                    && !Objects.equals(conversation.getConversationType(), "GS");
            case "ESTUDIANTE" -> Objects.equals(conversation.getStudentId(), me.profileId)
                    && !Objects.equals(conversation.getConversationType(), "GS");
            default -> false;
        };
        if (!ok) {
            throw new ForbiddenException("No participas en esta conversación");
        }
    }

    private SenderContext currentSender() {
        Long userId = currentUserId();
        if (access.isAdmin()) {
            String role = access.isSuperAdmin() ? "SUPER_ADMINISTRADOR" : "ADMINISTRATIVO";
            // profileId = userId for office (no academic profile)
            return new SenderContext(role, userId, userId != null ? userId : 0L);
        }
        if (access.isTeacher()) {
            return new SenderContext("DOCENTE", userId, access.requireTeacherId());
        }
        if (access.isGuardian()) {
            return new SenderContext("APODERADO", userId, access.requireGuardianId());
        }
        if (access.isStudent()) {
            return new SenderContext("ESTUDIANTE", userId, access.requireStudentId());
        }
        throw new ForbiddenException("No tiene acceso a mensajería");
    }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserPrincipal principal) {
            return principal.getUserId();
        }
        return null;
    }

    private ConversationDTO toConversationDto(Conversation conversation) {
        return toConversationDto(conversation, currentSender(), null, null);
    }

    private ConversationDTO toConversationDto(
            Conversation conversation,
            SenderContext me,
            Map<Long, LocalDateTime> lastReadByConversation,
            Map<Long, List<Message>> messagesByConversation) {
        ConversationDTO dto = new ConversationDTO();
        dto.setId(conversation.getId());
        dto.setConversationType(conversation.getConversationType());
        dto.setStudentId(conversation.getStudentId());
        dto.setTeacherId(conversation.getTeacherId());
        dto.setGuardianId(conversation.getGuardianId());
        dto.setUpdatedAt(conversation.getUpdatedAt());

        if (conversation.getStudentId() != null) {
            studentRepository.findById(conversation.getStudentId()).ifPresent(student ->
                    dto.setStudentName(fullName(student.getFirstName(), student.getLastName())));
        }
        if (conversation.getTeacherId() != null) {
            teacherRepository.findById(conversation.getTeacherId()).ifPresent(teacher ->
                    dto.setTeacherName(fullName(teacher.getFirstName(), teacher.getLastName())));
        }
        if (conversation.getGuardianId() != null) {
            guardianRepository.findById(conversation.getGuardianId()).ifPresent(guardian ->
                    dto.setGuardianName(fullName(guardian.getFirstName(), guardian.getLastName())));
        }

        dto.setTitle(buildTitle(conversation, dto, me));

        List<Message> threadMessages = messagesByConversation != null
                ? messagesByConversation.getOrDefault(conversation.getId(), List.of())
                : messageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId());

        if (!threadMessages.isEmpty()) {
            Message last = threadMessages.stream()
                    .max(Comparator.comparing(Message::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                    .orElse(threadMessages.get(threadMessages.size() - 1));
            String preview = last.getBody();
            dto.setLastMessagePreview(preview.length() > 80 ? preview.substring(0, 80) + "…" : preview);
        }

        LocalDateTime lastReadAt = lastReadByConversation != null
                ? lastReadByConversation.get(conversation.getId())
                : readStateRepository.findByConversationIdAndUserId(conversation.getId(), me.userId)
                        .map(ConversationReadState::getLastReadAt)
                        .orElse(null);
        dto.unreadCount = countUnread(threadMessages, me, lastReadAt);
        return dto;
    }

    private String buildTitle(Conversation conversation, ConversationDTO dto, SenderContext me) {
        boolean office = isOfficeRole(me.role);
        return switch (conversation.getConversationType()) {
            case TYPE_ADMIN_TEACHER -> office
                    ? "Docente: " + nullToDash(dto.getTeacherName())
                    : "Oficina / Coordinación";
            case TYPE_ADMIN_GUARDIAN -> office
                    ? "Apoderado: " + nullToDash(dto.getGuardianName())
                    : "Oficina / Coordinación";
            case TYPE_ADMIN_STUDENT -> office
                    ? "Alumno: " + nullToDash(dto.getStudentName())
                    : "Oficina / Coordinación";
            case TYPE_TEACHER_GUARDIAN -> me.role.equals("DOCENTE")
                    ? "Apoderado: " + nullToDash(dto.getGuardianName()) + " (" + nullToDash(dto.getStudentName()) + ")"
                    : "Docente: " + nullToDash(dto.getTeacherName()) + " (" + nullToDash(dto.getStudentName()) + ")";
            case TYPE_TEACHER_STUDENT -> me.role.equals("DOCENTE")
                    ? "Alumno: " + nullToDash(dto.getStudentName())
                    : "Docente: " + nullToDash(dto.getTeacherName());
            default -> "Conversación";
        };
    }

    private void markConversationRead(Long conversationId, SenderContext me) {
        if (me.userId == null) {
            return;
        }
        ConversationReadState state = readStateRepository
                .findByConversationIdAndUserId(conversationId, me.userId)
                .orElseGet(() -> {
                    ConversationReadState created = new ConversationReadState();
                    created.setConversationId(conversationId);
                    created.setUserId(me.userId);
                    return created;
                });
        state.setLastReadAt(LocalDateTime.now());
        readStateRepository.save(state);
    }

    private static long countUnread(List<Message> messages, SenderContext me, LocalDateTime lastReadAt) {
        return messages.stream()
                .filter(message -> !isMine(message, me))
                .filter(message -> lastReadAt == null
                        || (message.getCreatedAt() != null && message.getCreatedAt().isAfter(lastReadAt)))
                .count();
    }

    private static boolean isMine(Message message, SenderContext me) {
        if (isOfficeRole(me.role) && isOfficeRole(message.getSenderRole())
                && Objects.equals(message.getSenderUserId(), me.userId)) {
            return true;
        }
        return Objects.equals(message.getSenderRole(), me.role)
                && Objects.equals(message.getSenderProfileId(), me.profileId);
    }

    private MessageDTO toMessageDto(Message message, SenderContext me) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversationId());
        dto.setSenderRole(message.getSenderRole());
        dto.setSenderUserId(message.getSenderUserId());
        dto.setSenderProfileId(message.getSenderProfileId());
        dto.setBody(message.getBody());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setUpdatedAt(message.getUpdatedAt());
        boolean edited = message.getUpdatedAt() != null
                && message.getCreatedAt() != null
                && !message.getUpdatedAt().equals(message.getCreatedAt());
        dto.setEdited(edited);
        dto.setMine(Objects.equals(message.getSenderRole(), me.role)
                && Objects.equals(message.getSenderProfileId(), me.profileId));
        // office users share inbox: mark mine if same userId
        if (isOfficeRole(me.role) && isOfficeRole(message.getSenderRole())
                && Objects.equals(message.getSenderUserId(), me.userId)) {
            dto.setMine(true);
        }
        dto.setSenderName(resolveSenderName(message));
        return dto;
    }

    private void ensureMessageAuthor(Message message, SenderContext me) {
        boolean author;
        if (isOfficeRole(me.role) && isOfficeRole(message.getSenderRole())) {
            author = Objects.equals(message.getSenderUserId(), me.userId);
        } else {
            author = Objects.equals(message.getSenderRole(), me.role)
                    && Objects.equals(message.getSenderProfileId(), me.profileId);
        }
        if (!author) {
            throw new ForbiddenException("Solo puedes editar o eliminar tus propios mensajes");
        }
    }

    private static String requireMessageBody(SendMessageRequest request) {
        if (request == null || request.getBody() == null || request.getBody().isBlank()) {
            throw new BadRequestException("El mensaje no puede estar vacío");
        }
        String body = request.getBody().trim();
        if (body.length() > 2000) {
            throw new BadRequestException("El mensaje no puede superar 2000 caracteres");
        }
        return body;
    }

    private String resolveSenderName(Message message) {
        if (isOfficeRole(message.getSenderRole())) {
            return "Oficina";
        }
        return switch (message.getSenderRole()) {
            case "DOCENTE" -> teacherRepository.findById(message.getSenderProfileId())
                    .map(t -> fullName(t.getFirstName(), t.getLastName())).orElse("Docente");
            case "APODERADO" -> guardianRepository.findById(message.getSenderProfileId())
                    .map(g -> fullName(g.getFirstName(), g.getLastName())).orElse("Apoderado");
            case "ESTUDIANTE" -> studentRepository.findById(message.getSenderProfileId())
                    .map(s -> fullName(s.getFirstName(), s.getLastName())).orElse("Estudiante");
            default -> "Usuario";
        };
    }

    private MessageContactDTO baseContact(
            String type, Long studentId, String studentName, Long teacherId, Long guardianId) {
        MessageContactDTO dto = new MessageContactDTO();
        dto.setConversationType(type);
        dto.setStudentId(studentId);
        dto.setStudentName(studentName);
        dto.setTeacherId(teacherId);
        dto.setGuardianId(guardianId);
        return dto;
    }

    private static Long resolveStudentId(String type, Long studentId) {
        return switch (type) {
            case TYPE_ADMIN_STUDENT, TYPE_TEACHER_GUARDIAN, TYPE_TEACHER_STUDENT -> studentId;
            default -> null;
        };
    }

    private static Long resolveTeacherId(String type, Long teacherId) {
        return switch (type) {
            case TYPE_ADMIN_TEACHER, TYPE_TEACHER_GUARDIAN, TYPE_TEACHER_STUDENT -> teacherId;
            default -> null;
        };
    }

    private static Long resolveGuardianId(String type, Long guardianId) {
        return switch (type) {
            case TYPE_ADMIN_GUARDIAN, TYPE_TEACHER_GUARDIAN -> guardianId;
            default -> null;
        };
    }

    private static String buildThreadKey(String type, Long studentId, Long teacherId, Long guardianId) {
        return switch (type) {
            case TYPE_ADMIN_TEACHER -> "AD:" + teacherId;
            case TYPE_ADMIN_GUARDIAN -> "AG:" + guardianId;
            case TYPE_ADMIN_STUDENT -> "AS:" + studentId;
            case TYPE_TEACHER_GUARDIAN -> "TG:" + studentId + ":" + teacherId + ":" + guardianId;
            case TYPE_TEACHER_STUDENT -> "TS:" + studentId + ":" + teacherId;
            default -> throw new BadRequestException("Tipo de conversación no válido");
        };
    }

    private static String normalizeType(String type) {
        if (type == null || type.isBlank()) {
            throw new BadRequestException("conversationType es obligatorio");
        }
        String normalized = type.trim().toUpperCase();
        if (!ALLOWED_TYPES.contains(normalized)) {
            throw new BadRequestException("conversationType debe ser AD, AG, AS, TG o TS");
        }
        return normalized;
    }

    private static Long requirePositive(Long value, String field) {
        if (value == null || value <= 0) {
            throw new BadRequestException(field + " es obligatorio");
        }
        return value;
    }

    private static boolean isOfficeRole(String role) {
        return "ADMINISTRATIVO".equals(role)
                || "SUPER_ADMINISTRADOR".equals(role)
                || "ADMINISTRADOR".equals(role);
    }

    private static String fullName(String first, String last) {
        return ((first == null ? "" : first) + " " + (last == null ? "" : last)).trim();
    }

    private static String nullToDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }

    private record SenderContext(String role, Long userId, Long profileId) {}
}
