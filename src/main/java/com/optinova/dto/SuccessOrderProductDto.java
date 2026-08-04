package com.optinova.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO representing an individual product item from a successfully placed order.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuccessOrderProductDto {

    private String orderId;
    private Integer productId;
    private String name;
    private String description;
    private String category;
    private Integer quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal totalPrice;
    private String imageUrl;
    private String status;
    private String orderDate;
}
