package com.optinova.mapper;

import com.optinova.dto.CartItemDto;
import com.optinova.dto.CartResponse;
import com.optinova.entity.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper component converting CartItem entities to DTOs and building CartResponse summaries.
 */
@Component
public class CartMapper {

    public CartItemDto toDto(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }

        String primaryImage = null;
        if (cartItem.getProduct() != null && cartItem.getProduct().getImages() != null && !cartItem.getProduct().getImages().isEmpty()) {
            primaryImage = cartItem.getProduct().getImages().get(0).getImageUrl();
        }

        BigDecimal price = cartItem.getProduct() != null ? cartItem.getProduct().getPrice() : BigDecimal.ZERO;
        BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return CartItemDto.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProduct() != null ? cartItem.getProduct().getProductId() : null)
                .productName(cartItem.getProduct() != null ? cartItem.getProduct().getName() : null)
                .price(price)
                .quantity(cartItem.getQuantity())
                .totalPrice(itemTotal)
                .primaryImageUrl(primaryImage)
                .build();
    }

    public CartResponse toCartResponse(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return CartResponse.builder()
                    .items(Collections.emptyList())
                    .totalItems(0)
                    .grandTotal(BigDecimal.ZERO)
                    .build();
        }

        List<CartItemDto> itemDtos = cartItems.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        int totalItems = cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        BigDecimal grandTotal = itemDtos.stream()
                .map(CartItemDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .items(itemDtos)
                .totalItems(totalItems)
                .grandTotal(grandTotal)
                .build();
    }
}
