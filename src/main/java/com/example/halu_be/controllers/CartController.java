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
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;
    private final ProductService productService;

    @GetMapping("/{buyerId}")
    public List<CartItemDTO> getCartItems(@PathVariable Long buyerId) {
        Optional<User> buyer = userService.getUserById(buyerId);
        if (buyer.isEmpty()) return Collections.emptyList();
        return cartService.getCartItemDTOsByBuyer(buyer.get());
    }

    @PostMapping("/{buyerId}/add/{productId}")
    public ResponseEntity<?> addToCart(@PathVariable Long buyerId, @PathVariable Long productId) {
        Optional<User> buyer = userService.getUserById(buyerId);
        Optional<Product> product = productService.getProductById(productId);

        if (buyer.isEmpty() || product.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Sorry, something happened and we can't add your item. You can try again.");
        }

        try {
            CartItemDTO item = cartService.addProductToCart(buyer.get(), product.get());
            return ResponseEntity.ok(item);
        } catch (IllegalStateException e) {
            // 👇👇 This is the important part
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(e.getMessage()); // like "This product is out of stock..."
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Sorry, something went wrong. Please try again.");
        }
    }



    @PostMapping("/{buyerId}/remove/{productId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long buyerId, @PathVariable Long productId) {
        Optional<User> buyer = userService.getUserById(buyerId);
        Optional<Product> product = productService.getProductById(productId);

        if (buyer.isEmpty() || product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Unable to remove item. Buyer or product not found.");
        }

        // Frontend should prompt: "Are you sure you want to remove this product?"
        cartService.removeProductFromCart(buyer.get(), product.get());
        return ResponseEntity.ok("Item removed from cart.");
    }

    @PostMapping("/{buyerId}/clear")
    public ResponseEntity<?> clearCart(@PathVariable Long buyerId) {
        Optional<User> buyer = userService.getUserById(buyerId);
        if (buyer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Buyer not found.");
        }

        // Frontend should prompt: "Are you sure you want to clear the cart?"
        cartService.clearCart(buyer.get());
        return ResponseEntity.ok("Cart cleared successfully.");
    }
    
}
