package com.optinova.service;

import com.optinova.dto.AddToCartRequest;
import com.optinova.dto.ApiResponse;
import com.optinova.dto.CartResponse;
import com.optinova.dto.UpdateCartItemRequest;

/**
 * Service interface defining Shopping Cart operations for active user sessions.
 */
public interface CartService {

    CartResponse getUserCart(Integer userId);

    CartResponse addItemToCart(Integer userId, AddToCartRequest request);

    CartResponse updateCartItemQuantity(Integer userId, Integer cartItemId, UpdateCartItemRequest request);

    CartResponse removeCartItem(Integer userId, Integer cartItemId);

    ApiResponse<String> clearUserCart(Integer userId);
}
