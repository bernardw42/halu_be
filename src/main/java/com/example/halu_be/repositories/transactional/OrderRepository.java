package com.example.halu_be.repositories.transactional;

import com.example.halu_be.models.transactional.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
