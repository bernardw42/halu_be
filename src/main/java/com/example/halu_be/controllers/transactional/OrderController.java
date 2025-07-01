package com.example.halu_be.controllers.transactional;

import com.example.halu_be.dtos.OrderSummaryDTO;
import com.example.halu_be.models.User;
import com.example.halu_be.models.transactional.Order;
import com.example.halu_be.services.UserService;
import com.example.halu_be.services.transactional.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<String> confirmPayment(@PathVariable Long orderId, Authentication auth) {
        User buyer = userService.getUserByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        try {
            String result = orderService.confirmPayment(orderId, buyer);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{orderId}/ship")
    public ResponseEntity<String> confirmShipment(@PathVariable Long orderId, Authentication auth) {
        User seller = userService.getUserByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        try {
            String result = orderService.confirmShipment(orderId, seller);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId, Authentication auth) {
        User user = userService.getUserByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            String result = orderService.cancelOrder(orderId, user);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{orderId}/timing")
    public ResponseEntity<?> getRemainingTime(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            Map<String, Object> response = new HashMap<>();

            long secondsRemaining = Duration.between(
                    Instant.now(), order.getExpiresAt().toInstant()
            ).getSeconds();

            response.put("orderId", order.getId());
            response.put("status", order.getStatus());
            response.put("expiresAt", order.getExpiresAt());
            response.put("secondsRemaining", Math.max(0, secondsRemaining));

            if ("PENDING".equals(order.getStatus())) {
                response.put("timerType", "payment");
            } else if ("PAID".equals(order.getStatus())) {
                response.put("timerType", "shipment");
            } else {
                response.put("timerType", "none");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/buyer")
    public ResponseEntity<?> getOrdersByBuyer(Authentication auth) {
        User buyer = userService.getUserByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        List<OrderSummaryDTO> orders = orderService.getOrdersByBuyer(buyer);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/seller")
    public ResponseEntity<?> getOrdersBySeller(Authentication auth) {
        User seller = userService.getUserByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        List<OrderSummaryDTO> orders = orderService.getOrdersBySeller(seller);
        return ResponseEntity.ok(orders);
    }
}
