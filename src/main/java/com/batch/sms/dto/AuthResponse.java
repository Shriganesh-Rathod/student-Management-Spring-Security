package com.batch.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {

    // Generated JWT access token
    private String accessToken;

    // Token type expected by protected APIs
    private String tokenType;

    // Token validity in seconds
    private long expiresIn;
}
