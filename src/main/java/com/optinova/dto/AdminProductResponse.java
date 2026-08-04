package com.optinova.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO returning complete details of a product managed by Admin.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO response containing full product details for Admin view")
public class AdminProductResponse {

    @Schema(description = "Primary key product ID", example = "10")
    private Integer productId;

    @Schema(description = "Name of the product", example = "Aviator Polarized Sunglasses")
    private String name;

    @Schema(description = "Product description", example = "Premium anti-glare titanium frame sunglasses.")
    private String description;

    @Schema(description = "Unit price", example = "129.99")
    private BigDecimal price;

    @Schema(description = "Inventory stock count", example = "50")
    private Integer stock;

    @Schema(description = "Associated category ID", example = "1")
    private Integer categoryId;

    @Schema(description = "Associated category name", example = "Sunglasses")
    private String categoryName;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}
