package com.example.halu_be.services;

import com.example.halu_be.dtos.ProductDTO;
import com.example.halu_be.models.Product;
import com.example.halu_be.models.User;
import com.example.halu_be.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByOwner(User owner) {
        return productRepository.findByOwner(owner);
    }

    public Optional<Product> getProductByIdAndOwnerId(Long productId, Long ownerId) {
        return productRepository.findByIdAndOwnerId(productId, ownerId);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<ProductDTO> getProductsDTOByOwner(User owner) {
        return productRepository.findByOwner(owner).stream()
            .map(product -> new ProductDTO(
                product.getId(),
                product.getTitle(),
                product.getCategory(),
                product.getPrice(),
                product.getDescription(),
                product.getImageUrl()
            )).toList();
    }

    public Optional<ProductDTO> getProductDTOBySellerAndProductId(Long sellerId, Long productId) {
    return productRepository.findByIdAndOwnerId(productId, sellerId).map(product ->
        new ProductDTO(
            product.getId(),
            product.getTitle(),
            product.getCategory(),
            product.getPrice(),
            product.getDescription(),
            product.getImageUrl()
        )
    );
    }

    public Optional<Product> getProductById(Long productId) {
    return productRepository.findById(productId);
    }



}
