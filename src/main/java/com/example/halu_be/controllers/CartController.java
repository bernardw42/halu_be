package com.example.halu_be.controllers;

import com.example.halu_be.dtos.CartItemDTO;
import com.example.halu_be.models.Product;
import com.example.halu_be.models.User;
import com.example.halu_be.services.CartService;
import com.example.halu_be.services.ProductService;
import com.example.halu_be.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/buyer/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;
    private final ProductService productService;

    // 🔒 Resolved from JWT
    private User getCurrentBuyer(Authentication auth) {
        return userService.getUserByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
    }

    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getCartItems(Authentication auth) {
        User buyer = getCurrentBuyer(auth);
        List<CartItemDTO> items = cartService.getCartItemDTOsByBuyer(buyer);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<?> addToCart(Authentication auth, @PathVariable Long productId) {
        User buyer = getCurrentBuyer(auth);

        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }

        try {
            CartItemDTO dto = cartService.addProductToCart(buyer, productOpt.get());
            return ResponseEntity.ok(dto);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not add to cart.");
        }
    }

    @PostMapping("/remove/{productId}")
    public ResponseEntity<?> removeFromCart(Authentication auth, @PathVariable Long productId) {
        User buyer = getCurrentBuyer(auth);

        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }

        cartService.removeProductFromCart(buyer, productOpt.get());
        return ResponseEntity.ok("Product removed from cart.");
    }

    @PostMapping("/clear")
    public ResponseEntity<?> clearCart(Authentication auth) {
        User buyer = getCurrentBuyer(auth);
        cartService.clearCart(buyer);
        return ResponseEntity.ok("Cart cleared successfully.");
    }
}
