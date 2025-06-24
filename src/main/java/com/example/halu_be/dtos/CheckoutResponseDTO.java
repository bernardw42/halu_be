package com.example.halu_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
public class CheckoutResponseDTO {
    private Long orderId;
    private String status;
    private BigDecimal total;
    private List<ItemSummary> items;
    private Timestamp expiresAt;
    private long secondsRemaining;

    @Data
    @AllArgsConstructor
    public static class ItemSummary {
        private String productTitle;
        private int quantity;
        private BigDecimal unitPrice;
    }
}
