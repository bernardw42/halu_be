package com.example.halu_be.repositories;

import com.example.halu_be.models.CartItem;
import com.example.halu_be.models.Cart;
import com.example.halu_be.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product); // ✅ FIXED
}
