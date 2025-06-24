package com.example.halu_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderSummaryDTO {
    private Long orderId;
    private String status;
    private Timestamp createdAt;
    private Timestamp expiresAt;
    private BigDecimal totalPrice;
    private List<ItemDTO> items;

    @Data
    @AllArgsConstructor
    public static class ItemDTO {
        private String productTitle;
        private int quantity;
        private BigDecimal unitPrice;
    }
}
