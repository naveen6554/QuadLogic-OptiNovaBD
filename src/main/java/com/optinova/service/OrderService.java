package com.optinova.service;

import com.optinova.dto.*;

import java.util.List;

/**
 * Service interface defining Order placement and tracking.
 */
public interface OrderService {

    OrderDto createOrder(Integer userId, CreateOrderRequest request);

    List<OrderDto> getUserOrders(Integer userId);

    OrderDto getOrderById(Integer userId, String orderId);

    PageResponse<OrderDto> getAllOrders(int pageNo, int pageSize, String sortBy, String sortDir);

    OrderDto updateOrderStatus(String orderId, UpdateOrderStatusRequest request);

    UserOrdersResponse getUserSuccessOrders(Integer userId);
}
