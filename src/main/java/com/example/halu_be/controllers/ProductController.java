package com.example.halu_be.controllers;

import com.example.halu_be.dtos.ProductDTO;
import com.example.halu_be.models.Product;
import com.example.halu_be.models.User;
import com.example.halu_be.services.ProductService;
import com.example.halu_be.services.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * 🔹 Get all products by specific seller (no owner info)
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
     * 🔹 Get a single product by global ID (with owner)
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
     * 🔹 Get a specific product of a specific seller (no owner info)
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

    /**
     * 🔹 Create a product for a seller
     */
    @PostMapping("/{sellerId}")
    public ResponseEntity<?> createProduct(@PathVariable Long sellerId, @RequestBody Product product) {
        Optional<User> seller = userService.getUserById(sellerId);
        if (seller.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        if (seller.get().getRole() != User.Role.SELLER) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This user is a BUYER and cannot create products.");
        }
        product.setOwner(seller.get());
        Product saved = productService.saveProduct(product);
        return ResponseEntity.ok(saved);
    }

    /**
     * 🔹 Update product (only if it belongs to the seller)
     */
    @PutMapping("/{sellerId}/{productId}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long sellerId,
            @PathVariable Long productId,
            @RequestBody Product updatedProduct) {

        Optional<Product> existing = productService.getProductByIdAndOwnerId(productId, sellerId);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found or does not belong to this seller.");
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
     * 🔹 Delete product (only if it belongs to the seller)
     */
    @DeleteMapping("/{sellerId}/{productId}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable Long sellerId,
            @PathVariable Long productId) {

        Optional<Product> product = productService.getProductByIdAndOwnerId(productId, sellerId);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found or does not belong to this seller.");
        }

        productService.deleteProduct(productId);
        return ResponseEntity.ok("Product deleted successfully.");
    }
}
