package com.example.halu_be.repositories.transactional;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.halu_be.models.CartItem;
import com.example.halu_be.models.transactional.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<CartItem> findByCartId(Long cartId);
}
