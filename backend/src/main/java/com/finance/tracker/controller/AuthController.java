package com.finance.tracker.controller;

import com.finance.tracker.dto.AuthResponse;
import com.finance.tracker.dto.LoginRequest;
import com.finance.tracker.dto.RegisterRequest;
import com.finance.tracker.dto.UserDTO;
import com.finance.tracker.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Authentication Module
 * Handles user registration, login, and profile management
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${cors.allowed-origins}")
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * POST /api/auth/register - Register a new user
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /api/auth/register - Registering user: {}", request.getUsername());
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * POST /api/auth/login - Authenticate user
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - User: {}", request.getUsername());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /api/auth/logout - Logout user (handled on client side)
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        log.info("POST /api/auth/logout");
        return ResponseEntity.ok("Logged out successfully");
    }
    
    /**
     * GET /api/auth/profile - Get user profile
     */
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(@RequestAttribute("userId") String userId) {
        log.info("GET /api/auth/profile - User ID: {}", userId);
        UserDTO userDTO = authService.getUserProfile(userId);
        return ResponseEntity.ok(userDTO);
    }
}
