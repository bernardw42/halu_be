package com.example.halu_be.repositories.secondary;

import com.example.halu_be.models.secondary.LegacyCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LegacyCustomerRepository extends JpaRepository<LegacyCustomer, Integer> {
    Optional<LegacyCustomer> findByNationalId(String nationalId);
}
