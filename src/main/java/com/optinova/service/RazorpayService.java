package com.optinova.service;

import com.optinova.dto.CreateOrderRequest;
import com.optinova.dto.OrderDto;
import com.optinova.dto.PaymentVerificationRequest;
import com.optinova.dto.RazorpayOrderResponse;

/**
 * Service handling Razorpay payment gateway integration, order generation, and signature verification.
 */
public interface RazorpayService {

    /**
     * Creates DB order and corresponding Razorpay Order ID.
     */
    RazorpayOrderResponse initiateRazorpayCheckout(Integer userId, CreateOrderRequest request);

    /**
     * Verifies Razorpay payment HMAC signature and updates order status.
     */
    OrderDto verifyAndCompletePayment(Integer userId, PaymentVerificationRequest verificationRequest);
}
