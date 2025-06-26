package com.example.halu_be.services.secondary;

import com.example.halu_be.dtos.secondary.UserLegacyDTO;
import com.example.halu_be.models.User;
import com.example.halu_be.models.secondary.LegacyCustomer;
import com.example.halu_be.models.Cart;
import com.example.halu_be.repositories.UserRepository;
import com.example.halu_be.repositories.CartRepository;
import com.example.halu_be.repositories.secondary.LegacyCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CombineService {

    private final UserRepository userRepository;
    private final LegacyCustomerRepository legacyCustomerRepository;
    private final CartRepository cartRepository;

    public Optional<UserLegacyDTO> getCombinedInfoByLegacyId(int legacyCustomerId) {
        Optional<LegacyCustomer> legacyOpt = legacyCustomerRepository.findById(legacyCustomerId);
        if (legacyOpt.isEmpty()) return Optional.empty();

        LegacyCustomer legacy = legacyOpt.get();
        String username = legacy.getName();
        if (username == null || username.trim().isEmpty()) return Optional.empty();
        username = username.trim();

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return Optional.empty();

        User user = userOpt.get();

        Optional<Cart> cartOpt = cartRepository.findByBuyer(user);

        Long cartId = null;
        LocalDateTime cartCreatedAt = null;
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            cartId = cart.getId();
            cartCreatedAt = cart.getCreatedAt();
        }

        // Defensive null checks for legacy fields
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
    }
}
