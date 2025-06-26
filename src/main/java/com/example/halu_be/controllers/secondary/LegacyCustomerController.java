package com.example.halu_be.controllers.secondary;

import com.example.halu_be.models.secondary.LegacyCustomer;
import com.example.halu_be.services.secondary.LegacyCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/legacy")
@RequiredArgsConstructor
public class LegacyCustomerController {

    private final LegacyCustomerService legacyCustomerService;

    @GetMapping("/customer/{nationalId}")
    public ResponseEntity<?> getCustomerByNationalId(@PathVariable String nationalId) {
        Optional<LegacyCustomer> customer = legacyCustomerService.getByNationalId(nationalId);
        return customer.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
