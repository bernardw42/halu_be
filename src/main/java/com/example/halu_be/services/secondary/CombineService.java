package com.example.halu_be.services.secondary;

import com.example.halu_be.dtos.secondary.UserLegacyDTO;
import com.example.halu_be.models.User;
import com.example.halu_be.models.secondary.LegacyCustomer;
import com.example.halu_be.models.Cart;
import com.example.halu_be.repositories.UserRepository;
import com.example.halu_be.repositories.CartRepository;
import com.example.halu_be.repositories.secondary.LegacyCustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j // ✅ Add logging
public class CombineService {

    private final UserRepository userRepository;
    private final LegacyCustomerRepository legacyCustomerRepository;
    private final CartRepository cartRepository;

    public Optional<UserLegacyDTO> getCombinedInfoByLegacyId(int legacyCustomerId) {
        try {
            // ✅ Lookup legacy
            Optional<LegacyCustomer> legacyOpt = legacyCustomerRepository.findById(legacyCustomerId);
            if (legacyOpt.isEmpty()) {
                log.warn("Legacy customer with ID {} not found.", legacyCustomerId);
                return Optional.empty();
            }

            LegacyCustomer legacy = legacyOpt.get();
            String username = legacy.getName();
            if (username == null || username.trim().isEmpty()) {
                log.warn("Legacy customer ID {} has empty name.", legacyCustomerId);
                return Optional.empty();
            }
            username = username.trim();

            // ✅ Lookup modern user by username
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                log.warn("No modern user found for legacy name '{}'", username);
                return Optional.empty();
            }

            User user = userOpt.get();

            // ✅ Lookup cart
            Optional<Cart> cartOpt = cartRepository.findByBuyer(user);

            Long cartId = null;
            LocalDateTime cartCreatedAt = null;
            if (cartOpt.isPresent()) {
                Cart cart = cartOpt.get();
                cartId = cart.getId();
                cartCreatedAt = cart.getCreatedAt();
            }

            // ✅ Defensive checks
            String nationalId = legacy.getNationalId() != null ? legacy.getNationalId() : "";
            String email = legacy.getEmail() != null ? legacy.getEmail() : "";

            return Optional.of(new UserLegacyDTO(
                    user.getId(),
                    user.getUsername(),
                    nationalId,
                    user.getRole() != null ? user.getRole().name() : "",
                    user.getProfileImageUrl(),
                    email,
                    cartId,
                    cartCreatedAt
            ));
        } catch (Exception e) {
            log.error("Error combining legacy ID {}: {}", legacyCustomerId, e.getMessage(), e);
            throw new RuntimeException("Failed to combine legacy and user info.", e);
        }
    }
}
