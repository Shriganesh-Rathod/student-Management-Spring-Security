package com.batch.sms.service.impl;

import com.batch.sms.dto.PageResponse;
import com.batch.sms.dto.StudentRequestDto;
import com.batch.sms.dto.StudentResponseDto;
import com.batch.sms.entity.Address;
import com.batch.sms.entity.Course;
import com.batch.sms.entity.Department;
import com.batch.sms.entity.Student;
import com.batch.sms.exception.DuplicateResourceException;
import com.batch.sms.exception.ResourceNotFoundException;
import com.batch.sms.repository.CourseRepository;
import com.batch.sms.repository.DepartmentRepository;
import com.batch.sms.repository.StudentRepository;
import com.batch.sms.service.FileStorageService;
import com.batch.sms.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public StudentResponseDto createStudent(StudentRequestDto request) {
        log.info("Creating student with email: {}", request.getEmail());

        if (studentRepository.existsByEmail(request.getEmail())) {
            log.warn("Duplicate email attempted during create: {}", request.getEmail());
            throw new DuplicateResourceException("Student already exists with email: " + request.getEmail());
        }

        Student student = toEntity(request);

        if (request.getDepartmentId() != null) {
            student.setDepartment(findDepartment(request.getDepartmentId()));
        }
        if (request.getCourseIds() != null && !request.getCourseIds().isEmpty()) {
            student.setCourses(resolveCourses(request.getCourseIds()));
        }

        Student saved = studentRepository.save(student);
        log.info("Student created successfully with id: {}", saved.getId());

        return toResponse(saved);
    }

    @Override
    public StudentResponseDto getStudentById(Long id) {
        log.debug("Fetching student with id: {}", id);
        return toResponse(findStudent(id));
    }

    @Override
    public PageResponse<StudentResponseDto> getStudents(String search, Pageable pageable) {
        Page<Student> page;

        if (StringUtils.hasText(search)) {
            log.debug("Searching students with keyword '{}', page {}", search, pageable.getPageNumber());
            page = studentRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                    search, search, pageable);
        } else {
            log.debug("Fetching all students, page {}", pageable.getPageNumber());
            page = studentRepository.findAll(pageable);
        }

        return PageResponse.from(page.map(this::toResponse));
    }

    @Override
    @Transactional
    public StudentResponseDto updateStudent(Long id, StudentRequestDto request) {
        log.info("Updating student with id: {}", id);
        Student student = findStudent(id);

        if (studentRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            log.warn("Duplicate email attempted during update of id {}: {}", id, request.getEmail());
            throw new DuplicateResourceException("Another student already uses email: " + request.getEmail());
        }

        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setAddress(toAddress(request));

        student.setDepartment(request.getDepartmentId() != null
                ? findDepartment(request.getDepartmentId())
                : null);

        if (request.getCourseIds() != null) {
            student.setCourses(resolveCourses(request.getCourseIds()));
        }

        Student updated = studentRepository.save(student);
        log.info("Student with id {} updated successfully", updated.getId());

        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        log.info("Deleting student with id: {}", id);
        studentRepository.delete(findStudent(id));
        log.info("Student with id {} deleted successfully", id);
    }

    @Override
    @Transactional
    public StudentResponseDto uploadImage(Long id, MultipartFile file) {
        Student student = findStudent(id);
        String storedFilename = fileStorageService.store(file, String.valueOf(id));
        student.setImagePath(storedFilename);
        Student updated = studentRepository.save(student);
        log.info("Image uploaded for student id {}: {}", id, storedFilename);
        return toResponse(updated);
    }

    // ---------- helpers ----------

    private Student findStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Student not found with id: {}", id);
                    return new ResourceNotFoundException("Student not found with id: " + id);
                });
    }

    private Department findDepartment(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
    }

    private Set<Course> resolveCourses(Set<Long> courseIds) {
        Set<Course> courses = new HashSet<>(courseRepository.findAllById(courseIds));
        if (courses.size() != courseIds.size()) {
            throw new ResourceNotFoundException("One or more course ids do not exist: " + courseIds);
        }
        return courses;
    }

    private Address toAddress(StudentRequestDto dto) {
        return Address.builder()
                .street(dto.getStreet())
                .city(dto.getCity())
                .state(dto.getState())
                .pincode(dto.getPincode())
                .country(dto.getCountry())
                .build();
    }

    private Student toEntity(StudentRequestDto dto) {
        return Student.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .dateOfBirth(dto.getDateOfBirth())
                .address(toAddress(dto))
                .build();
    }

    private StudentResponseDto toResponse(Student student) {
        Address address = student.getAddress();
        Department department = student.getDepartment();

        return StudentResponseDto.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .phone(student.getPhone())
                .dateOfBirth(student.getDateOfBirth())
                .imageUrl(student.getImagePath() == null ? null : "/images/" + student.getImagePath())
                .street(address == null ? null : address.getStreet())
                .city(address == null ? null : address.getCity())
                .state(address == null ? null : address.getState())
                .pincode(address == null ? null : address.getPincode())
                .country(address == null ? null : address.getCountry())
                .departmentId(department == null ? null : department.getId())
                .departmentName(department == null ? null : department.getName())
                .courseTitles(student.getCourses() == null
                        ? Set.of()
                        : student.getCourses().stream().map(Course::getTitle).collect(Collectors.toSet()))
                .build();
    }
}
