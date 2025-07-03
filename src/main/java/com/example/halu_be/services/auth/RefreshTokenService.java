package com.example.halu_be.services.auth;

import com.example.halu_be.models.auth.RefreshToken;
import com.example.halu_be.models.User;
import com.example.halu_be.repositories.auth.RefreshTokenRepository;
import com.example.halu_be.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    @Value("${jwt.refreshExpirationMs:604800000}") // 7 days default
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshToken createRefreshToken(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
            refreshToken.setToken(UUID.randomUUID().toString());

            return refreshTokenRepository.save(refreshToken);
        } catch (Exception e) {
            log.error("Failed to create refresh token for user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Could not create refresh token.", e);
        }
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            log.warn("Refresh token expired for user ID {}", token.getUser().getId());
            throw new RuntimeException("Refresh token expired. Please login again.");
        }
        return token;
    }

    @Transactional(transactionManager = "transactionManager")
    public int deleteByUserId(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return refreshTokenRepository.deleteByUser(user);
        } catch (Exception e) {
            log.error("Failed to delete refresh tokens for user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Could not logout user properly.", e);
        }
    }
}
