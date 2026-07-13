package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.model.Course;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
import cl.duoc.libroDigital.academicService.service.CourseService;
import cl.duoc.libroDigital.academicService.dto.CourseDTO;
import cl.duoc.libroDigital.academicService.security.AcademicAccessService;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    private final CatalogLookupService catalogs;
    private final AcademicAccessService access;

    public CourseController(
            CourseService courseService,
            CatalogLookupService catalogs,
            AcademicAccessService access) {
        this.courseService = courseService;
        this.catalogs = catalogs;
        this.access = access;
    }

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private CourseDTO toDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setName(course.getName());
        dto.setGrade(catalogs.label("education_levels", course.getLevelId()));
        LocalDate start = catalogs.academicYearStartDate(course.getAcademicYearId());
        dto.setAcademicYear(start != null ? start.format(DATE_FORMAT) : null);
        dto.setShift(catalogs.code("shifts", course.getShiftId()));
        dto.setHeadTeacherId(course.getHeadTeacherId());
        dto.setMaxCapacity(course.getMaxCapacity());
        dto.setClassroom(course.getClassroom());
        dto.setLevel(catalogs.educationStage(course.getLevelId()));
        dto.setCourseStatus(catalogs.code("course_statuses", course.getCourseStatusId()));
        dto.setCreatedAt(course.getCreatedAt() != null ? course.getCreatedAt().format(DATETIME_FORMAT) : null);
        dto.setUpdatedAt(course.getUpdatedAt() != null ? course.getUpdatedAt().format(DATETIME_FORMAT) : null);
        return dto;
    }

    private Course toEntity(CourseDTO dto) {
        Course course = new Course();
        course.setId(dto.getId());
        course.setName(dto.getName());
        course.setLevelId(catalogs.educationLevelIdFromLabel(dto.getGrade()));
        if (dto.getAcademicYear() != null && !dto.getAcademicYear().isBlank()) {
            course.setAcademicYearId(catalogs.academicYearIdFromDate(LocalDate.parse(dto.getAcademicYear(), DATE_FORMAT)));
        }
        if (dto.getShift() != null && !dto.getShift().isBlank()) {
            course.setShiftId(catalogs.requireId("shifts", dto.getShift()));
        }
        course.setHeadTeacherId(dto.getHeadTeacherId());
        course.setMaxCapacity(dto.getMaxCapacity());
        course.setClassroom(dto.getClassroom());
        course.setCourseStatusId(catalogs.requireId("course_statuses", dto.getCourseStatus()));
        return course;
    }

    @PostMapping
    public CourseDTO createCourse(@RequestBody CourseDTO dto) {
        access.requireSuperAdmin();
        Course created = courseService.createCourse(toEntity(dto));
        return toDTO(created);
    }

    @GetMapping
    public List<CourseDTO> getAllCourses() {
        if (access.isAdmin()) {
            return courseService.getAllCourses().stream().map(this::toDTO).collect(Collectors.toList());
        }
        if (access.isStudent()) {
            Long studentId = access.requireStudentId();
            Set<Long> allowed = access.studentCourseIds(studentId);
            return courseService.getAllCourses().stream()
                    .filter(course -> allowed.contains(course.getId()))
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }
        if (access.isGuardian()) {
            Long guardianId = access.requireGuardianId();
            Set<Long> allowed = access.guardianCourseIds(guardianId);
            return courseService.getAllCourses().stream()
                    .filter(course -> allowed.contains(course.getId()))
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }
        Long teacherId = access.requireTeacherId();
        Set<Long> allowed = access.teacherCourseIds(teacherId);
        return courseService.getAllCourses().stream()
                .filter(course -> allowed.contains(course.getId()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public CourseDTO updateCourse(@PathVariable Long id, @RequestBody CourseDTO dto) {
        access.requireAdmin();
        Course updated = courseService.updateCourse(id, toEntity(dto));
        return toDTO(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable Long id) {
        access.requireSuperAdmin();
        courseService.deleteCourse(id);
    }

    @GetMapping("/{id}")
    public CourseDTO getCourse(@PathVariable Long id) {
        access.ensureCanReadCourse(id);
        return courseService.getCourseById(id).map(this::toDTO).orElse(null);
    }
}
