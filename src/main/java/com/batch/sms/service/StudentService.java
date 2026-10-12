package com.batch.sms.service;

import com.batch.sms.dto.PageResponse;
import com.batch.sms.dto.StudentRequestDto;
import com.batch.sms.dto.StudentResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface StudentService {

    StudentResponseDto createStudent(StudentRequestDto request);

    StudentResponseDto getStudentById(Long id);

    PageResponse<StudentResponseDto> getStudents(String search, Pageable pageable);

    StudentResponseDto updateStudent(Long id, StudentRequestDto request);

    void deleteStudent(Long id);

    StudentResponseDto uploadImage(Long id, MultipartFile file);
}
