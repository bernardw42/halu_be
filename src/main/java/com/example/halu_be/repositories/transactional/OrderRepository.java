package com.example.halu_be.repositories.transactional;

import com.example.halu_be.models.User;
import com.example.halu_be.models.transactional.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyerOrderByCreatedAtDesc(User buyer);
}
