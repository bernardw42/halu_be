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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/buyer/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;
    private final ProductService productService;

    private User getCurrentUser(Authentication auth) {
        return userService.getUserByUsername(auth.getName()).orElseThrow();
    }

    @GetMapping
    public List<CartItemDTO> getCartItems(Authentication auth) {
        User buyer = getCurrentUser(auth);
        return cartService.getCartItemDTOsByBuyer(buyer);
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<?> addToCart(Authentication auth, @PathVariable Long productId) {
        User buyer = getCurrentUser(auth);
        Optional<Product> product = productService.getProductById(productId);

        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Product not found.");
        }

        try {
            CartItemDTO item = cartService.addProductToCart(buyer, product.get());
            return ResponseEntity.ok(item);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong. Try again.");
        }
    }

    @PostMapping("/remove/{productId}")
    public ResponseEntity<?> removeFromCart(Authentication auth, @PathVariable Long productId) {
        User buyer = getCurrentUser(auth);
        Optional<Product> product = productService.getProductById(productId);

        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Product not found.");
        }

        cartService.removeProductFromCart(buyer, product.get());
        return ResponseEntity.ok("Item removed from cart.");
    }

    @PostMapping("/clear")
    public ResponseEntity<?> clearCart(Authentication auth) {
        User buyer = getCurrentUser(auth);
        cartService.clearCart(buyer);
        return ResponseEntity.ok("Cart cleared successfully.");
    }
}
