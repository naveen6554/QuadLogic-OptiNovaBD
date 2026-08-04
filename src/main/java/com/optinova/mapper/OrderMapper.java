package com.optinova.mapper;

import com.optinova.dto.OrderDto;
import com.optinova.dto.OrderItemDto;
import com.optinova.entity.Order;
import com.optinova.entity.OrderItem;
import com.optinova.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper component converting Order and OrderItem Entities to DTOs.
 */
@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final ProductImageRepository productImageRepository;

    public OrderItemDto toOrderItemDto(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        com.optinova.entity.Product product = orderItem.getProduct();
        String primaryImage = null;
        String description = null;
        String categoryName = null;

        if (product != null) {
            description = product.getDescription();
            if (product.getCategory() != null) {
                categoryName = product.getCategory().getCategoryName();
            }
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                primaryImage = product.getImages().get(0).getImageUrl();
            }
            if ((primaryImage == null || primaryImage.isBlank()) && product.getProductId() != null) {
                List<com.optinova.entity.ProductImage> pImages = productImageRepository.findByProductProductId(product.getProductId());
                if (pImages != null && !pImages.isEmpty()) {
                    primaryImage = pImages.get(0).getImageUrl();
                }
            }
        }

        return OrderItemDto.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrder() != null ? orderItem.getOrder().getOrderId() : null)
                .productId(product != null ? product.getProductId() : null)
                .productName(product != null ? product.getName() : null)
                .description(description)
                .categoryName(categoryName)
                .quantity(orderItem.getQuantity())
                .pricePerUnit(orderItem.getPricePerUnit())
                .totalPrice(orderItem.getTotalPrice())
                .primaryImageUrl(primaryImage)
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
                .orderId(order.getOrderId())
                .userId(order.getUser() != null ? order.getUser().getUserId() : null)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .orderItems(itemDtos)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
