package com.batch.sms.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CourseRequestDto {

    @NotBlank(message = "Course title is required")
    @Size(max = 100, message = "Course title must be up to 100 characters")
    private String title;

    @NotBlank(message = "Course code is required")
    @Size(max = 20, message = "Course code must be up to 20 characters")
    private String code;

    @NotNull(message = "Credits is required")
    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 10, message = "Credits must be at most 10")
    private Integer credits;

    @NotNull(message = "Department id is required")
    private Long departmentId;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
}
