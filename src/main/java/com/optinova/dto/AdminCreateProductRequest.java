package com.optinova.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for creating a new product by an Administrator.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO request payload for creating a new product")
public class AdminCreateProductRequest {

    @NotBlank(message = "Product name is required")
    @Schema(description = "Name of the optical product", example = "Aviator Polarized Sunglasses")
    private String name;

    @NotBlank(message = "Product description is required")
    @Schema(description = "Detailed product description", example = "Premium anti-glare titanium frame sunglasses.")
    private String description;

    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    @Schema(description = "Unit price of the product", example = "129.99")
    private BigDecimal price;

    @NotNull(message = "Initial stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Schema(description = "Available inventory stock quantity", example = "50")
    private Integer stock;

    @NotNull(message = "Category ID is required")
    @Schema(description = "ID of the assigned category", example = "1")
    private Integer categoryId;

    @Schema(description = "Product image URL", example = "https://images.unsplash.com/photo-1591076482161-42ce6da69f67")
    private String imageUrl;

    @Schema(description = "List of product image URLs")
    private java.util.List<String> imageUrls;
}

