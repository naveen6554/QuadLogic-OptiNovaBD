package com.optinova.service;

import com.optinova.dto.*;

import java.util.List;

/**
 * Service interface defining Order placement, user order tracking, and administrative fulfillment contracts.
 */
public interface OrderService {

    OrderDto createOrder(Long userId, CreateOrderRequest request);

    List<OrderDto> getUserOrders(Long userId);

    OrderDto getOrderById(Long userId, Long orderId);

    PageResponse<OrderDto> getAllOrders(int pageNo, int pageSize, String sortBy, String sortDir);

    OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);

    OrderDto updatePaymentStatus(Long orderId, UpdatePaymentStatusRequest request);
}
