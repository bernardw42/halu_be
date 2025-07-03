package com.example.halu_be.services.secondary;

import com.example.halu_be.dtos.secondary.EmailCreatedDTO;
import com.example.halu_be.repositories.UserRepository;
import com.example.halu_be.repositories.secondary.LegacyCustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailCombineService {

    private final UserRepository userRepository;
    private final LegacyCustomerRepository legacyCustomerRepository;

    public Optional<EmailCreatedDTO> getUserEmailInfoByLegacyId(int legacyCustomerId) {
        try {
            // fetch all matching rows (should be 0 or 1)
            List<Object[]> legacyRows =
                legacyCustomerRepository.findUsernameAndEmailByIdManual(legacyCustomerId);

            if (legacyRows.isEmpty()) {
                log.warn("Legacy ID {} not found.", legacyCustomerId);
                return Optional.empty();
            }

            // grab the first (and only) row
            Object[] row = legacyRows.get(0);
            String username = (String) row[0];
            String email    = (String) row[1];

            if (username == null || username.isBlank()) {
                log.warn("Legacy username empty for ID {}", legacyCustomerId);
                return Optional.empty();
            }
            username = username.trim();
            log.info("Resolved legacy username='{}', email='{}'", username, email);

            // now fetch just the created_at timestamp
            Optional<LocalDateTime> createdAtOpt =
                userRepository.findCreatedAtByUsernameManual(username);

            if (createdAtOpt.isEmpty()) {
                log.warn("No user found for legacy username '{}'", username);
                return Optional.empty();
            }
            log.info("Matched user '{}' created_at={}", username, createdAtOpt.get());

            return Optional.of(new EmailCreatedDTO(
                username,
                createdAtOpt.get(),
                email
            ));

        } catch (Exception e) {
            log.error("Error combining legacy[{}] → user data", legacyCustomerId, e);
            throw new RuntimeException("Could not combine user and legacy info.", e);
        }
    }
}
