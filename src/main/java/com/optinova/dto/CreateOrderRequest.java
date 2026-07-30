package com.optinova.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for creating an order from active shopping cart items.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotBlank(message = "Shipping address is required")
    @Size(max = 1000, message = "Shipping address must not exceed 1000 characters")
    private String shippingAddress;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
}
