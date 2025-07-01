package com.example.halu_be.repositories.auth;

import com.example.halu_be.models.auth.RefreshToken;
import com.example.halu_be.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(User user);
}
