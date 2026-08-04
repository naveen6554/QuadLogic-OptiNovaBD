package com.optinova.dto;

import com.optinova.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object representing detailed order responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {

    private String orderId;
    private Integer userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private List<OrderItemDto> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
