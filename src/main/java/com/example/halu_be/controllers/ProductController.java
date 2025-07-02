package com.example.halu_be.controllers;

import com.example.halu_be.dtos.ProductDTO;
import com.example.halu_be.models.Product;
import com.example.halu_be.models.User;
import com.example.halu_be.services.ProductService;
import com.example.halu_be.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // ✅ Add logging if needed for controller

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j // ✅ Enables log.info / log.error if needed here
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    /**
     * ✅ Public: Get ALL products from ALL sellers.
     * 👉 No try/catch here — relies on Service + ExceptionHandler for errors.
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> dtos = productService.getAllProductDTOs();
        return ResponseEntity.ok(dtos);
    }

    /**
     * ✅ Public: Get ALL products by specific seller ID.
     * 👉 Example of simple input validation: checking seller exists.
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<?> getProductsBySeller(@PathVariable Long sellerId) {
        Optional<User> sellerOpt = userService.getUserById(sellerId);

        if (sellerOpt.isEmpty()) {
            log.warn("Seller with ID {} not found", sellerId); // ✅ Optional: log input problem
            return ResponseEntity.notFound().build();
        }

        List<ProductDTO> dtos = productService.getProductsDTOByOwner(sellerOpt.get());
        return ResponseEntity.ok(dtos);
    }

    /**
     * ✅ Public: Get a single product by its ID (all sellers).
     */
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable Long productId) {
        Optional<Product> productOpt = productService.getProductById(productId);

        if (productOpt.isEmpty()) {
            log.warn("Product with ID {} not found", productId);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(productOpt.get());
    }

    /**
     * ✅ Public: Get a specific product by a specific seller.
     */
    @GetMapping("/{sellerId}/{productId}")
    public ResponseEntity<?> getProductBySellerAndProductId(
            @PathVariable Long sellerId,
            @PathVariable Long productId) {

        Optional<ProductDTO> dtoOpt = productService.getProductDTOBySellerAndProductId(sellerId, productId);

        if (dtoOpt.isEmpty()) {
            log.warn("Product with ID {} not found for seller ID {}", productId, sellerId);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dtoOpt.get());
    }
}
