package com.example.halu_be.services;

import com.example.halu_be.dtos.ProductDTO;
import com.example.halu_be.exceptions.EntityNotFoundException;
import com.example.halu_be.models.Product;
import com.example.halu_be.models.User;
import com.example.halu_be.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            throw new EntityNotFoundException("No products available at the moment.");
        }
        return products;
    }

    public List<Product> getProductsByOwner(User owner) {
        List<Product> products = productRepository.findByOwner(owner);
        if (products.isEmpty()) {
            throw new EntityNotFoundException("No products found for this seller.");
        }
        return products;
    }

    public Optional<Product> getProductByIdAndOwnerId(Long productId, Long ownerId) {
        return productRepository.findByIdAndOwnerId(productId, ownerId);
    }

    /**
     * ✅ This method uses the **primary** transaction manager explicitly.
     */
    @Transactional("transactionManager")
    public Product saveProduct(Product product) {
        validateProductFields(product);

        try {
            return productRepository.save(product);
        } catch (Exception e) {
            e.printStackTrace(); // Console debug
            throw new RuntimeException("Failed to save product: " + e.getMessage(), e);
        }
    }

    @Transactional("transactionManager")
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete. Product does not exist.");
        }
        try {
            productRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete product: " + e.getMessage(), e);
        }
    }

    public List<ProductDTO> getProductsDTOByOwner(User owner) {
        List<Product> products = getProductsByOwner(owner);
        return products.stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ProductDTO> getAllProductDTOs() {
        List<Product> products = getAllProducts();
        return products.stream()
                .map(this::toDTO)
                .toList();
    }

    public Optional<ProductDTO> getProductDTOBySellerAndProductId(Long sellerId, Long productId) {
        Optional<Product> optionalProduct = productRepository.findByIdAndOwnerId(productId, sellerId);
        return optionalProduct.map(this::toDTO);
    }

    public Optional<Product> getProductById(Long productId) {
        return productRepository.findById(productId);
    }

    private ProductDTO toDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getTitle(),
                product.getCategory(),
                product.getPrice(),
                product.getDescription(),
                product.getImageUrl(),
                product.getQuantity()
        );
    }

    private void validateProductFields(Product product) {
        if (product.getTitle() == null || product.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title must not be empty.");
        }
        if (product.getCategory() == null || product.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Category must not be empty.");
        }
        if (product.getDescription() == null || product.getDescription().trim().length() < 10) {
            throw new IllegalArgumentException("Description must be at least 10 characters.");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
        if (product.getImageUrl() == null || product.getImageUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL must not be empty.");
        }
        try {
            new URL(product.getImageUrl());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Image URL is not a valid URL.");
        }
    }
}
