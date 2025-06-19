package com.example.halu_be.dtos;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String title;
    private String category;
    private BigDecimal price;
    private String description;
    private String imageUrl;
    private Integer quantity;
}
