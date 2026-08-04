package com.optinova.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object containing Razorpay payment authorization details for signature verification.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentVerificationRequest {

    @NotBlank(message = "Razorpay Order ID is required")
    private String razorpayOrderId;

    @NotBlank(message = "Razorpay Payment ID is required")
    private String razorpayPaymentId;

    @NotBlank(message = "Razorpay Signature is required")
    private String razorpaySignature;

    @NotBlank(message = "Database Order ID is required")
    private String dbOrderId;
}
