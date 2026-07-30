package com.optinova.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object representing an item snapshot within a placed order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {

    private Long id;
    private Long productId;
    private String productName;
    private String productBrand;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}
