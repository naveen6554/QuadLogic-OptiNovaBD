package com.optinova.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing product gallery images in API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageDto {

    private Long id;
    private String imageUrl;
    private boolean isPrimary;
    private Long productId;
}
