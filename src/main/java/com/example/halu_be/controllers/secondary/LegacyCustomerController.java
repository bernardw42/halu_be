package com.example.halu_be.controllers.secondary;

import com.example.halu_be.dtos.secondary.EmailCreatedDTO;
import com.example.halu_be.dtos.secondary.UserLegacyDTO;
import com.example.halu_be.models.secondary.LegacyCustomer;
import com.example.halu_be.services.secondary.CombineService;
import com.example.halu_be.services.secondary.EmailCombineService;
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
    private final CombineService combineService;
    private final EmailCombineService emailCombineService;


    @GetMapping("/customer/{nationalId}")
    public ResponseEntity<?> getCustomerByNationalId(@PathVariable String nationalId) {
        Optional<LegacyCustomer> customer = legacyCustomerService.getByNationalId(nationalId);
        return customer.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/combined/id/{legacyCustomerId}") // ✅ clean URL
    public ResponseEntity<?> getUserWithLegacyInfoByLegacyId(@PathVariable int legacyCustomerId) {
        Optional<UserLegacyDTO> combined = combineService.getCombinedInfoByLegacyId(legacyCustomerId); // ✅ non-static call
        return combined.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404)
                        .body("No matching user or legacy record found for that ID."));
    }

    @GetMapping("/email-info/id/{legacyCustomerId}")
    public ResponseEntity<?> getEmailInfo(@PathVariable int legacyCustomerId) {
        Optional<EmailCreatedDTO> emailInfo = emailCombineService.getUserEmailInfoByLegacyId(legacyCustomerId);
        return emailInfo.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404)
                        .body("No matching user or legacy record found."));
    }

}
