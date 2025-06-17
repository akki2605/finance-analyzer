package com.codeyantratech.financeanalyzer.controller;

import com.codeyantratech.financeanalyzer.dto.AuthResponse;
import com.codeyantratech.financeanalyzer.dto.LoginRequest;
import com.codeyantratech.financeanalyzer.dto.SignupRequest;
import com.codeyantratech.financeanalyzer.model.User;
import com.codeyantratech.financeanalyzer.security.JwtUtils;
import com.codeyantratech.financeanalyzer.security.UserPrincipal;
import com.codeyantratech.financeanalyzer.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for handling authentication operations.
 * Provides endpoints for user login and registration.
 * Uses JWT tokens for authentication.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    /**
     * Authenticates a user and generates a JWT token.
     * 
     * @param loginRequest Contains username and password for authentication
     * @return ResponseEntity containing JWT token and user information if successful
     * @throws org.springframework.security.core.AuthenticationException if authentication fails
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userPrincipal);
        
        User user = userService.getCurrentUser(userPrincipal.getUsername());

        return ResponseEntity.ok(AuthResponse.builder()
                .token(jwt)
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .message("Login successful")
                .build());
    }

    /**
     * Registers a new user account.
     * Validates that username and email are not already taken.
     * Automatically logs in the user after successful registration.
     *
     * @param signupRequest Contains user registration information
     * @return ResponseEntity containing JWT token and user information if successful
     * @throws RuntimeException if username or email is already taken
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        User user = userService.createUser(
            signupRequest.getUsername(),
            signupRequest.getEmail(),
            signupRequest.getPassword(),
            signupRequest.getFirstName(),
            signupRequest.getLastName()
        );

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                signupRequest.getUsername(),
                signupRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userPrincipal);

        return ResponseEntity.ok(AuthResponse.builder()
                .token(jwt)
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .message("User registered successfully")
                .build());
    }
} 