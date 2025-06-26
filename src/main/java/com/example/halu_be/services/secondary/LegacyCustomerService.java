package com.example.halu_be.services.secondary;

import com.example.halu_be.models.secondary.LegacyCustomer;
import com.example.halu_be.repositories.secondary.LegacyCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LegacyCustomerService {

    private final LegacyCustomerRepository legacyCustomerRepository;

    public Optional<LegacyCustomer> getByNationalId(String nationalId) {
        return legacyCustomerRepository.findByNationalId(nationalId);
    }
}
