package com.optinova.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object representing optical product details in API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    private Long id;
    private String name;
    private String description;
    private String brand;
    private String frameType;
    private String frameShape;
    private String gender;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer stockQuantity;
    private boolean isFeatured;
    private boolean isActive;
    private CategoryDto category;
    private String primaryImageUrl;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
}
