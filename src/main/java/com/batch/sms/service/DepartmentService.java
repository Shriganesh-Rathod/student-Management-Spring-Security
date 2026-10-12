package com.batch.sms.service;

import com.batch.sms.dto.DepartmentRequestDto;
import com.batch.sms.dto.DepartmentResponseDto;

import java.util.List;

public interface DepartmentService {

    DepartmentResponseDto createDepartment(DepartmentRequestDto request);

    DepartmentResponseDto getDepartmentById(Long id);

    List<DepartmentResponseDto> getAllDepartments();

    DepartmentResponseDto updateDepartment(Long id, DepartmentRequestDto request);

    void deleteDepartment(Long id);
}
