package com.example.halu_be.controllers;

import com.example.halu_be.dtos.ProductDTO;
import com.example.halu_be.models.Product;
import com.example.halu_be.models.User;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    /**
     * 🔹 Get all products from all sellers
     */
    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProductDTOs();
    }

    /**
     * 🔹 Get all products by specific seller (public)
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<?> getProductsBySeller(@PathVariable Long sellerId) {
        Optional<User> seller = userService.getUserById(sellerId);
        if (seller.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Seller not found.");
        }
        List<ProductDTO> productDTOs = productService.getProductsDTOByOwner(seller.get());
        return ResponseEntity.ok(productDTOs);
    }

    /**
     * 🔹 Get a single product by ID
     */
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable Long productId) {
        Optional<Product> product = productService.getProductById(productId);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }
        return ResponseEntity.ok(product.get());
    }

    /**
     * 🔹 Get a specific product of a specific seller (public)
     */
    @GetMapping("/{sellerId}/{productId}")
    public ResponseEntity<?> getProductBySellerAndProductId(
            @PathVariable Long sellerId,
            @PathVariable Long productId) {
        Optional<ProductDTO> productDTO = productService.getProductDTOBySellerAndProductId(sellerId, productId);
        if (productDTO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found or does not belong to this seller.");
        }
        return ResponseEntity.ok(productDTO.get());
    }
}
