package com.example.halu_be.controllers.auth;

import com.example.halu_be.config.auth.JwtProvider;
import com.example.halu_be.dtos.auth.AuthResponse;
import com.example.halu_be.models.auth.RefreshToken;
import com.example.halu_be.repositories.UserRepository;
import com.example.halu_be.services.auth.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newJwt = jwtProvider.generateToken(user);
                    return ResponseEntity.ok(new AuthResponse(
                            newJwt,
                            refreshToken,
                            user.getId(),
                            user.getRole().name()
                    ));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        RefreshToken token = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        refreshTokenService.deleteByUserId(token.getUser().getId());

        return ResponseEntity.ok("Log out successful. Refresh token deleted.");
    }
}
