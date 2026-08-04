package com.optinova.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object representing product details in API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    private Integer productId;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private CategoryDto category;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Integer getId() {
        return productId;
    }

    public Integer getStockQuantity() {
        return stock;
    }
}
