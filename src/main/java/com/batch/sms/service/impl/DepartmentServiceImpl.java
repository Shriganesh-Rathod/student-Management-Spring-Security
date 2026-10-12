package com.batch.sms.service.impl;

import com.batch.sms.dto.DepartmentRequestDto;
import com.batch.sms.dto.DepartmentResponseDto;
import com.batch.sms.entity.Department;
import com.batch.sms.exception.DuplicateResourceException;
import com.batch.sms.exception.ResourceNotFoundException;
import com.batch.sms.repository.DepartmentRepository;
import com.batch.sms.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Departments change rarely but are read on almost every student/course
 * request, so they're a good first candidate for caching:
 * - getAllDepartments() / getDepartmentById(): cached, served from Redis
 *   after the first call.
 * - create/update/delete: evict the cache so the next read rebuilds it
 *   from the database rather than serving stale data.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentServiceImpl.class);
    private static final String CACHE_NAME = "departments";

    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public DepartmentResponseDto createDepartment(DepartmentRequestDto request) {
        log.info("Creating department with code: {}", request.getCode());

        if (departmentRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Department already exists with code: " + request.getCode());
        }

        Department department = Department.builder()
                .name(request.getName())
                .code(request.getCode())
                .build();

        Department saved = departmentRepository.save(department);
        log.info("Department created with id: {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "#id")
    public DepartmentResponseDto getDepartmentById(Long id) {
        log.debug("Fetching department with id: {} (cache miss if you see this log)", id);
        return toResponse(findDepartment(id));
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'all'")
    public List<DepartmentResponseDto> getAllDepartments() {
        log.debug("Fetching all departments from DB (cache miss if you see this log)");
        return departmentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public DepartmentResponseDto updateDepartment(Long id, DepartmentRequestDto request) {
        Department department = findDepartment(id);

        if (departmentRepository.existsByCodeAndIdNot(request.getCode(), id)) {
            throw new DuplicateResourceException("Another department already uses code: " + request.getCode());
        }

        department.setName(request.getName());
        department.setCode(request.getCode());

        Department updated = departmentRepository.save(department);
        log.info("Department with id {} updated", updated.getId());
        return toResponse(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void deleteDepartment(Long id) {
        Department department = findDepartment(id);
        // cascade = ALL + orphanRemoval on Department.courses means every
        // course under this department is deleted along with it.
        departmentRepository.delete(department);
        log.info("Department with id {} deleted (its courses cascaded)", id);
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
    }

    private DepartmentResponseDto toResponse(Department department) {
        return DepartmentResponseDto.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .courseCount(department.getCourses() == null ? 0 : department.getCourses().size())
                .build();
    }
}
