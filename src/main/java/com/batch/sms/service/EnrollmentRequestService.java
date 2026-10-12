package com.batch.sms.service;

import com.batch.sms.dto.EnrollmentRequestDto;
import com.batch.sms.dto.EnrollmentResponseDto;

import java.util.List;

public interface EnrollmentRequestService {

    EnrollmentResponseDto apply(EnrollmentRequestDto request);

    EnrollmentResponseDto getById(Long id);

    List<EnrollmentResponseDto> getAll();

    EnrollmentResponseDto approve(Long id);

    EnrollmentResponseDto reject(Long id);
}
