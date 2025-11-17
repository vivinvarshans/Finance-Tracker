package com.finance.tracker.service;

import com.finance.tracker.dto.*;
import com.finance.tracker.exception.AuthenticationException;
import com.finance.tracker.exception.ResourceAlreadyExistsException;
import com.finance.tracker.exception.ResourceNotFoundException;
import com.finance.tracker.model.User;
import com.finance.tracker.repository.UserRepository;
import com.finance.tracker.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for Authentication operations
 * Implements business logic for user registration and login
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    /**
     * Register a new user
     * Validates that username and email are unique
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user: {}", request.getUsername());
        
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException("User", "username", request.getUsername());
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("User", "email", request.getEmail());
        }
        
        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        
        user = userRepository.save(user);
        log.info("User registered successfully: {} with ID: {}", user.getUsername(), user.getId());
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getEmail());
        
        // Build response
        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
        
        return AuthResponse.builder()
                .token(token)
                .message("Registration successful")
                .user(userDTO)
                .build();
    }
    
    /**
     * Authenticate user and generate token
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());
        
        // Find user by username
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password for user: {}", request.getUsername());
            throw new AuthenticationException("Invalid credentials");
        }
        
        log.info("User logged in successfully: {} with ID: {}", user.getUsername(), user.getId());
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getEmail());
        
        // Build response
        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
        
        return AuthResponse.builder()
                .token(token)
                .message("Login successful")
                .user(userDTO)
                .build();
    }
    
    /**
     * Get user profile
     */
    @Transactional(readOnly = true)
    public UserDTO getUserProfile(String userId) {
        log.info("Fetching profile for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
