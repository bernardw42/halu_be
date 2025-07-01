package com.example.halu_be.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private Long userId;
    private String role; // Or use User.Role enum if you prefer
}
