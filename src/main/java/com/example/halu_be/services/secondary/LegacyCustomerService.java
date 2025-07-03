package com.example.halu_be.services.secondary;

import com.example.halu_be.models.secondary.LegacyCustomer;
import com.example.halu_be.repositories.secondary.LegacyCustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j // ✅ Add logging
public class LegacyCustomerService {

    private final LegacyCustomerRepository legacyCustomerRepository;

    public Optional<LegacyCustomer> getByNationalId(String nationalId) {
        try {
            Optional<LegacyCustomer> legacyOpt = legacyCustomerRepository.findByNationalId(nationalId);
            if (legacyOpt.isEmpty()) {
                log.warn("Legacy customer with national ID '{}' not found.", nationalId);
            }
            return legacyOpt;
        } catch (Exception e) {
            log.error("Failed to fetch legacy customer by national ID '{}': {}", nationalId, e.getMessage(), e);
            throw new RuntimeException("Could not fetch legacy customer data.", e);
        }
    }
}
