package com.batch.sms.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class StudentResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String imageUrl;

    private String street;
    private String city;
    private String state;
    private String pincode;
    private String country;

    private Long departmentId;
    private String departmentName;

    private Set<String> courseTitles;
}
