package com.example.halu_be.dtos.secondary;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EmailCreatedDTO {
    private String username;
    private LocalDateTime createdAt;
    private String email;
}