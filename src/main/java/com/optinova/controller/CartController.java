package com.optinova.controller;

import com.optinova.constants.AppConstants;
import com.optinova.dto.AddToCartRequest;
import com.optinova.dto.ApiResponse;
import com.optinova.dto.CartResponse;
import com.optinova.dto.UpdateCartItemRequest;
import com.optinova.security.CustomUserDetails;
import com.optinova.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller exposing Shopping Cart management APIs for authenticated users.
 */
@RestController
@RequestMapping(AppConstants.CART_BASE_PATH)
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Cart Module", description = "REST APIs for Shopping Cart Item Management")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get User Cart", description = "Retrieves the active user's shopping cart items, total item count, and grand total.")
    public ResponseEntity<ApiResponse<CartResponse>> getUserCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        CartResponse cart = cartService.getUserCart(userDetails.getUser().getId());
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved successfully", cart));
    }

    @PostMapping("/items")
    @Operation(summary = "Add Item To Cart", description = "Adds an optical product item to the user's shopping cart.")
    public ResponseEntity<ApiResponse<CartResponse>> addItemToCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AddToCartRequest request) {
        CartResponse updatedCart = cartService.addItemToCart(userDetails.getUser().getId(), request);
        return new ResponseEntity<>(ApiResponse.success("Item added to cart successfully", updatedCart), HttpStatus.CREATED);
    }

    @PutMapping("/items/{cartItemId}")
    @Operation(summary = "Update Cart Item Quantity", description = "Updates the quantity of a specific item in the user's shopping cart.")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItemQuantity(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartResponse updatedCart = cartService.updateCartItemQuantity(userDetails.getUser().getId(), cartItemId, request);
        return ResponseEntity.ok(ApiResponse.success("Cart item quantity updated successfully", updatedCart));
    }

    @DeleteMapping("/items/{cartItemId}")
    @Operation(summary = "Remove Item From Cart", description = "Removes a specific item from the user's shopping cart.")
    public ResponseEntity<ApiResponse<CartResponse>> removeCartItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long cartItemId) {
        CartResponse updatedCart = cartService.removeCartItem(userDetails.getUser().getId(), cartItemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart successfully", updatedCart));
    }

    @DeleteMapping
    @Operation(summary = "Clear Cart", description = "Clears all items from the user's shopping cart.")
    public ResponseEntity<ApiResponse<String>> clearUserCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        ApiResponse<String> response = cartService.clearUserCart(userDetails.getUser().getId());
        return ResponseEntity.ok(response);
    }
}
