package com.optinova.mapper;

import com.optinova.dto.CartItemDto;
import com.optinova.dto.CartResponse;
import com.optinova.entity.CartItem;
import com.optinova.entity.ProductImage;
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
        if (cartItem.getProduct() != null && cartItem.getProduct().getImages() != null) {
            primaryImage = cartItem.getProduct().getImages().stream()
                    .filter(ProductImage::isPrimary)
                    .map(ProductImage::getImageUrl)
                    .findFirst()
                    .orElse(!cartItem.getProduct().getImages().isEmpty()
                            ? cartItem.getProduct().getImages().get(0).getImageUrl() : null);
        }

        BigDecimal effectivePrice = (cartItem.getProduct().getDiscountPrice() != null &&
                cartItem.getProduct().getDiscountPrice().compareTo(BigDecimal.ZERO) > 0)
                ? cartItem.getProduct().getDiscountPrice()
                : cartItem.getProduct().getPrice();

        return CartItemDto.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .productBrand(cartItem.getProduct().getBrand())
                .price(effectivePrice)
                .quantity(cartItem.getQuantity())
                .totalPrice(cartItem.getTotalPrice())
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

        BigDecimal grandTotal = cartItems.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .items(itemDtos)
                .totalItems(totalItems)
                .grandTotal(grandTotal)
                .build();
    }
}
