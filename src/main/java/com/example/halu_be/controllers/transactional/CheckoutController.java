package com.example.halu_be.controllers.transactional;

import com.example.halu_be.models.transactional.Order;
import com.example.halu_be.services.transactional.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping("/{buyerId}")
    public ResponseEntity<Order> checkout(@PathVariable Long buyerId) {
        Order order = checkoutService.checkout(buyerId);
        return ResponseEntity.ok(order);
    }
}
