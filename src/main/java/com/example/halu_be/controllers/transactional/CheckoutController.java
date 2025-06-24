package com.example.halu_be.controllers.transactional;

import com.example.halu_be.dtos.CheckoutResponseDTO;
import com.example.halu_be.services.transactional.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    // ✅ POST /api/checkout/{buyerId} → Create checkout
    @PostMapping("/{buyerId}")
    public ResponseEntity<?> checkout(@PathVariable Long buyerId) {
        try {
            CheckoutResponseDTO response = checkoutService.checkout(buyerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ✅ GET /api/checkout/{orderId} → Show products and timer before paying
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getCheckoutDetails(@PathVariable Long orderId) {
        try {
            CheckoutResponseDTO response = checkoutService.getCheckoutDetails(orderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
