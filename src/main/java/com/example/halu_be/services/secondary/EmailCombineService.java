package com.example.halu_be.services.secondary;

import com.example.halu_be.dtos.secondary.EmailCreatedDTO;
import com.example.halu_be.models.User;
import com.example.halu_be.models.secondary.LegacyCustomer;
import com.example.halu_be.repositories.UserRepository;
import com.example.halu_be.repositories.secondary.LegacyCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailCombineService {

    private final UserRepository userRepository;
    private final LegacyCustomerRepository legacyCustomerRepository;
    

    public Optional<EmailCreatedDTO> getUserEmailInfoByLegacyId(int legacyCustomerId) {
        Optional<LegacyCustomer> legacyOpt = legacyCustomerRepository.findById(legacyCustomerId);
        if (legacyOpt.isEmpty()) return Optional.empty();

        LegacyCustomer legacy = legacyOpt.get();
        String username = legacy.getName(); // or getUsername()

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return Optional.empty();

        User user = userOpt.get();

        return Optional.of(new EmailCreatedDTO(
            username,
            user.getCreatedAt(),
            legacy.getEmail() // ✅ use getter method
        ));

    }
}
