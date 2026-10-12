package com.batch.sms.service;

import com.batch.sms.dto.CourseRequestDto;
import com.batch.sms.dto.CourseResponseDto;

import java.util.List;

public interface CourseService {

    CourseResponseDto createCourse(CourseRequestDto request);

    CourseResponseDto getCourseById(Long id);

    List<CourseResponseDto> getAllCourses();

    CourseResponseDto updateCourse(Long id, CourseRequestDto request);

    void deleteCourse(Long id);
}
