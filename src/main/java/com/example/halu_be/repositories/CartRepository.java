package com.example.halu_be.repositories;

import com.example.halu_be.models.Cart;
import com.example.halu_be.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByBuyer(User buyer);
}
