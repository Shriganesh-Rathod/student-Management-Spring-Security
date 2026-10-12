package com.batch.sms.service.impl;

import com.batch.sms.dto.CourseRequestDto;
import com.batch.sms.dto.CourseResponseDto;
import com.batch.sms.entity.Course;
import com.batch.sms.entity.Department;
import com.batch.sms.exception.DuplicateResourceException;
import com.batch.sms.exception.ResourceNotFoundException;
import com.batch.sms.repository.CourseRepository;
import com.batch.sms.repository.DepartmentRepository;
import com.batch.sms.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);
    private static final String CACHE_NAME = "courses";

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public CourseResponseDto createCourse(CourseRequestDto request) {
        log.info("Creating course with code: {}", request.getCode());

        if (courseRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Course already exists with code: " + request.getCode());
        }

        Department department = findDepartment(request.getDepartmentId());

        Course course = Course.builder()
                .title(request.getTitle())
                .code(request.getCode())
                .credits(request.getCredits())
                .department(department)
                .capacity(request.getCapacity())
                .build();

        Course saved = courseRepository.save(course);
        log.info("Course created with id: {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "#id")
    public CourseResponseDto getCourseById(Long id) {
        log.debug("Fetching course with id: {} (cache miss if you see this log)", id);
        return toResponse(findCourse(id));
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'all'")
    public List<CourseResponseDto> getAllCourses() {
        log.debug("Fetching all courses from DB (cache miss if you see this log)");
        return courseRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public CourseResponseDto updateCourse(Long id, CourseRequestDto request) {
        Course course = findCourse(id);

        if (courseRepository.existsByCodeAndIdNot(request.getCode(), id)) {
            throw new DuplicateResourceException("Another course already uses code: " + request.getCode());
        }

        Department department = findDepartment(request.getDepartmentId());

        course.setTitle(request.getTitle());
        course.setCode(request.getCode());
        course.setCredits(request.getCredits());
        course.setDepartment(department);
        course.setCapacity(request.getCapacity());
        // enrolledCount is deliberately NOT updated here — it only ever
        // changes inside EnrollmentRequestServiceImpl.approve()

        Course updated = courseRepository.save(course);
        log.info("Course with id {} updated", updated.getId());
        return toResponse(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void deleteCourse(Long id) {
        courseRepository.delete(findCourse(id));
        log.info("Course with id {} deleted", id);
    }

    private Course findCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    private Department findDepartment(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
    }

    private CourseResponseDto toResponse(Course course) {
        return CourseResponseDto.builder()
                .id(course.getId())
                .title(course.getTitle())
                .code(course.getCode())
                .credits(course.getCredits())
                .departmentId(course.getDepartment().getId())
                .departmentName(course.getDepartment().getName())
                .capacity(course.getCapacity())
                .enrolledCount(course.getEnrolledCount())
                .build();
    }
}
