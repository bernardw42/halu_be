package com.example.halu_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemDTO {
    private Long id;
    private ProductDTO product;
    private int quantity;
}
