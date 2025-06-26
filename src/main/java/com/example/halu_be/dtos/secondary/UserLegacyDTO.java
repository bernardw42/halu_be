package com.example.halu_be.dtos.secondary;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserLegacyDTO {
    private Long userId;
    private String username;
    private String nationalId;
    private String role;
    private String profileImageUrl;
    private String email;
    private Long cartId; // 🔥 New
    private LocalDateTime cartCreatedAt; // 🔥 New
}
