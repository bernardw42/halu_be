package com.example.halu_be.repositories;

import com.example.halu_be.models.Product;
import com.example.halu_be.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByOwner(User owner);
    Optional<Product> findByIdAndOwnerId(Long id, Long ownerId);
}
