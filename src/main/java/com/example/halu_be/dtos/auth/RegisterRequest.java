package com.example.halu_be.dtos.auth;

import com.example.halu_be.models.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String password;
    private User.Role role; // Uses inner enum
    private String nationalId;
    private String profileImageUrl; // Optional
}
