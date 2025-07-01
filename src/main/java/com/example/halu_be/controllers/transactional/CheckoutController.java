package com.example.halu_be.controllers.transactional;

import com.example.halu_be.dtos.CheckoutResponseDTO;
import com.example.halu_be.models.User;
import com.example.halu_be.services.UserService;
import com.example.halu_be.services.transactional.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buyer/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final UserService userService;

    /**
     * ✅ POST → Create checkout for authenticated buyer
     */
    @PostMapping
    public ResponseEntity<?> checkout(Authentication auth) {
        try {
            User buyer = userService.getUserByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Buyer not found"));

            CheckoutResponseDTO response = checkoutService.checkout(buyer);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * ✅ GET → Get checkout details for authenticated buyer
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getCheckoutDetails(@PathVariable Long orderId, Authentication auth) {
        try {
            User buyer = userService.getUserByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Buyer not found"));

            CheckoutResponseDTO response = checkoutService.getCheckoutDetails(orderId, buyer);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
