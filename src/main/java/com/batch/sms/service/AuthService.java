package com.batch.sms.service;

import com.batch.sms.dto.AuthResponse;
import com.batch.sms.dto.LoginRequest;
import com.batch.sms.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // Access token validity configured in application.properties
    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    // Authenticate the user and generate an access token
    public AuthResponse login(LoginRequest request) {

        // Spring Security verifies the username and password
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        // Get the authenticated user's details
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Generate a JWT access token
        String accessToken = jwtService.generateToken(userDetails);

        // Return the token and its validity in seconds
        return new AuthResponse(
                accessToken,
                "Bearer",
                accessTokenExpiration / 1000
        );
    }
}
