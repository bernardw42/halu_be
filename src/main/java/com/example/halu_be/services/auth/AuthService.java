package com.example.halu_be.services.auth;

import com.example.halu_be.dtos.auth.AuthResponse;
import com.example.halu_be.dtos.auth.LoginRequest;
import com.example.halu_be.dtos.auth.RegisterRequest;
import com.example.halu_be.models.User;
import com.example.halu_be.models.auth.RefreshToken;
import com.example.halu_be.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.example.halu_be.config.auth.JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Registration failed: Username '{}' already taken", request.getUsername());
            throw new RuntimeException("Username already taken");
        }

        if (request.getNationalId() != null && userRepository.findByNationalId(request.getNationalId()).isPresent()) {
            log.warn("Registration failed: National ID '{}' already registered", request.getNationalId());
            throw new RuntimeException("National ID already registered");
        }

        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(request.getRole());
            user.setNationalId(request.getNationalId());
            user.setProfileImageUrl(request.getProfileImageUrl());

            userRepository.save(user);
        } catch (Exception e) {
            log.error("Registration failed for username '{}': {}", request.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Registration failed. Please try again.", e);
        }
    }

    public AuthResponse login(LoginRequest request) {
        try {
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> {
                        log.warn("Login failed: User '{}' not found", request.getUsername());
                        return new RuntimeException("User not found");
                    });

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("Login failed: Invalid credentials for user '{}'", request.getUsername());
                throw new RuntimeException("Invalid credentials");
            }

            String jwt = jwtProvider.generateToken(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

            return new AuthResponse(
                    jwt,
                    refreshToken.getToken(),
                    user.getId(),
                    user.getRole().name()
            );
        } catch (Exception e) {
            log.error("Login failed for username '{}': {}", request.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Login failed. Please try again.", e);
        }
    }
}
