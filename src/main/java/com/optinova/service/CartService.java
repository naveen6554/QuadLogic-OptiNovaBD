package com.optinova.service;

import com.optinova.dto.AddToCartRequest;
import com.optinova.dto.ApiResponse;
import com.optinova.dto.CartResponse;
import com.optinova.dto.UpdateCartItemRequest;

/**
 * Service interface defining Shopping Cart operations for active user sessions.
 */
public interface CartService {

    CartResponse getUserCart(Long userId);

    CartResponse addItemToCart(Long userId, AddToCartRequest request);

    CartResponse updateCartItemQuantity(Long userId, Long cartItemId, UpdateCartItemRequest request);

    CartResponse removeCartItem(Long userId, Long cartItemId);

    ApiResponse<String> clearUserCart(Long userId);
}
