package com.optinova.mapper;

import com.optinova.dto.OrderDto;
import com.optinova.dto.OrderItemDto;
import com.optinova.entity.Order;
import com.optinova.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper component converting Order and OrderItem Entities to DTOs.
 */
@Component
public class OrderMapper {

    public OrderItemDto toOrderItemDto(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        return OrderItemDto.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProduct() != null ? orderItem.getProduct().getId() : null)
                .productName(orderItem.getProduct() != null ? orderItem.getProduct().getName() : null)
                .productBrand(orderItem.getProduct() != null ? orderItem.getProduct().getBrand() : null)
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .subtotal(orderItem.getSubtotal())
                .build();
    }

    public OrderDto toOrderDto(Order order) {
        if (order == null) {
            return null;
        }

        List<OrderItemDto> itemDtos = (order.getOrderItems() != null)
                ? order.getOrderItems().stream().map(this::toOrderItemDto).collect(Collectors.toList())
                : Collections.emptyList();

        return OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .shippingAddress(order.getShippingAddress())
                .paymentMethod(order.getPaymentMethod())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .totalAmount(order.getTotalAmount())
                .orderItems(itemDtos)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
