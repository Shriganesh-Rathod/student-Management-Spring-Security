package com.batch.sms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    // Username provided by the user during login
    @NotBlank(message = "Username is required")
    private String username;

    // Password provided by the user during login
    @NotBlank(message = "Password is required")
    private String password;
}
