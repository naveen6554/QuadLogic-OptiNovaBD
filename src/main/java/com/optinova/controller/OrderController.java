package com.optinova.controller;

import com.optinova.constants.AppConstants;
import com.optinova.dto.*;
import com.optinova.security.CustomUserDetails;
import com.optinova.service.OrderService;
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

import java.util.List;

/**
 * REST Controller exposing Order placement, order history, and administrative fulfillment endpoints.
 */
@RestController
@RequestMapping(AppConstants.ORDER_BASE_PATH)
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Order Module", description = "REST APIs for Checkout, Order Tracking, and Admin Fulfillment")
public class OrderController {

    private final OrderService orderService;
    private final com.optinova.service.RazorpayService razorpayService;

    @PostMapping("/razorpay/create")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Initiate Razorpay Checkout", description = "Creates DB order and generates Razorpay Order ID for payment gateway.")
    public ResponseEntity<ApiResponse<RazorpayOrderResponse>> createRazorpayOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateOrderRequest request) {
        RazorpayOrderResponse response = razorpayService.initiateRazorpayCheckout(userDetails.getUser().getUserId(), request);
        return new ResponseEntity<>(ApiResponse.success("Razorpay order created successfully", response), HttpStatus.CREATED);
    }

    @PostMapping("/razorpay/verify")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Verify Razorpay Payment Signature", description = "Verifies Razorpay HMAC signature and marks order as paid.")
    public ResponseEntity<ApiResponse<OrderDto>> verifyRazorpayPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PaymentVerificationRequest request) {
        OrderDto order = razorpayService.verifyAndCompletePayment(userDetails.getUser().getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Payment verified and order completed successfully", order));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create Order (Checkout)", description = "Places an order from the user's active cart items and deducts stock inventory.")
    public ResponseEntity<ApiResponse<OrderDto>> createOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateOrderRequest request) {
        OrderDto order = orderService.createOrder(userDetails.getUser().getUserId(), request);
        return new ResponseEntity<>(ApiResponse.success("Order placed successfully", order), HttpStatus.CREATED);
    }

    @GetMapping("/my-orders")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get User Orders", description = "Retrieves order history for the authenticated user.")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getUserOrders(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<OrderDto> orders = orderService.getUserOrders(userDetails.getUser().getUserId());
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orders));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Order By Id", description = "Retrieves specific order details by order ID.")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String orderId) {
        OrderDto order = orderService.getOrderById(userDetails.getUser().getUserId(), orderId);
        return ResponseEntity.ok(ApiResponse.success("Order details retrieved successfully", order));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get All Orders (Admin)", description = "Retrieves paginated list of all customer orders. Requires ADMIN role.")
    public ResponseEntity<ApiResponse<PageResponse<OrderDto>>> getAllOrders(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        PageResponse<OrderDto> orders = orderService.getAllOrders(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("All customer orders retrieved successfully", orders));
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Order Status (Admin)", description = "Updates order fulfillment status (PENDING, SUCCESS, FAILED). Requires ADMIN role.")
    public ResponseEntity<ApiResponse<OrderDto>> updateOrderStatus(
            @PathVariable String orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderDto order = orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", order));
    }
}
