package com.optinova.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object returned to frontend to initiate Razorpay checkout modal.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RazorpayOrderResponse {
    private String razorpayOrderId;
    private String dbOrderId;
    private String keyId;
    private Long amount; // in paise (e.g. 50000 = ₹500.00)
    private String currency;
    private BigDecimal displayAmount;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
}
