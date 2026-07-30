package com.optinova.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object for creating or updating an Optical Product.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 150, message = "Product name must be between 2 and 150 characters")
    private String name;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @Size(max = 100, message = "Brand must not exceed 100 characters")
    private String brand;

    @Size(max = 50, message = "Frame type must not exceed 50 characters")
    private String frameType;

    @Size(max = 50, message = "Frame shape must not exceed 50 characters")
    private String frameShape;

    @Size(max = 20, message = "Gender must not exceed 20 characters")
    private String gender;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "Discount price must be zero or positive")
    private BigDecimal discountPrice;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @Builder.Default
    private boolean isFeatured = false;

    @Builder.Default
    private boolean isActive = true;

    private List<String> imageUrls;
}
