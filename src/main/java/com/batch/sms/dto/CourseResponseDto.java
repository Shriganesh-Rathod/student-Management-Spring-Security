package com.batch.sms.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseResponseDto {

    private Long id;
    private String title;
    private String code;
    private Integer credits;
    private Long departmentId;
    private String departmentName;
    private Integer capacity;
    private Integer enrolledCount;
}
