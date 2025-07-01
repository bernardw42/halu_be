package com.example.halu_be.controllers;

import com.example.halu_be.models.Product;
import com.example.halu_be.models.User;
import com.example.halu_be.services.ProductService;
import com.example.halu_be.services.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/seller/products")
@RequiredArgsConstructor
public class SellerProductController {

    private final ProductService productService;
    private final UserService userService;

    private User getCurrentSeller(Authentication auth) {
        return userService.getUserByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Seller not found"));
    }

    /**
     * 🔹 Create product (seller only)
     */
    @PostMapping
    public ResponseEntity<?> createProduct(Authentication auth, @RequestBody Product product) {
        User seller = getCurrentSeller(auth);

        if (seller.getRole() != User.Role.SELLER) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("This user is not a SELLER.");
        }

        product.setOwner(seller);
        Product saved = productService.saveProduct(product);
        return ResponseEntity.ok(saved);
    }

    /**
     * 🔹 Update product (must belong to seller)
     */
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(
            Authentication auth,
            @PathVariable Long productId,
            @RequestBody Product updatedProduct) {

        User seller = getCurrentSeller(auth);
        Optional<Product> existing = productService.getProductByIdAndOwnerId(productId, seller.getId());

        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found or does not belong to you.");
        }

        Product product = existing.get();
        product.setTitle(updatedProduct.getTitle());
        product.setCategory(updatedProduct.getCategory());
        product.setPrice(updatedProduct.getPrice());
        product.setDescription(updatedProduct.getDescription());
        product.setImageUrl(updatedProduct.getImageUrl());
        product.setQuantity(updatedProduct.getQuantity());

        Product saved = productService.saveProduct(product);
        return ResponseEntity.ok(saved);
    }

    /**
     * 🔹 Delete product (must belong to seller)
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(Authentication auth, @PathVariable Long productId) {
        User seller = getCurrentSeller(auth);
        Optional<Product> product = productService.getProductByIdAndOwnerId(productId, seller.getId());

        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found or does not belong to you.");
        }

        productService.deleteProduct(productId);
        return ResponseEntity.ok("Product deleted successfully.");
    }
}
