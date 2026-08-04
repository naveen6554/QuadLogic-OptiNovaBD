package com.optinova.controller;

import com.optinova.dto.UserOrdersResponse;
import com.optinova.security.CustomUserDetails;
import com.optinova.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller exposing Order Management endpoint GET /api/orders for authenticated users.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Order Management Module", description = "REST APIs for User Order Retrieval")
public class UserOrderController {

    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get User Success Orders", description = "Retrieves all SUCCESS orders belonging to the authenticated user.")
    public ResponseEntity<UserOrdersResponse> getUserSuccessOrders(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserOrdersResponse response = orderService.getUserSuccessOrders(userDetails.getUser().getUserId());
        return ResponseEntity.ok(response);
    }
}
