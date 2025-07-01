package com.example.halu_be.services.auth;

import com.example.halu_be.dtos.auth.AuthResponse;
import com.example.halu_be.dtos.auth.LoginRequest;
import com.example.halu_be.dtos.auth.RegisterRequest;
import com.example.halu_be.models.User;
import com.example.halu_be.models.auth.RefreshToken;
import com.example.halu_be.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.example.halu_be.config.auth.JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        if (request.getNationalId() != null && userRepository.findByNationalId(request.getNationalId()).isPresent()) {
            throw new RuntimeException("National ID already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setNationalId(request.getNationalId());
        user.setProfileImageUrl(request.getProfileImageUrl());

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
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
    }

}
