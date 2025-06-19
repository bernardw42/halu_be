package com.example.halu_be.models.transactional;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

import com.example.halu_be.models.Product;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;
}
