package com.optinova.service.impl;

import com.optinova.dto.*;
import com.optinova.entity.CartItem;
import com.optinova.entity.Order;
import com.optinova.entity.OrderItem;
import com.optinova.entity.Product;
import com.optinova.entity.User;
import com.optinova.entity.enums.OrderStatus;
import com.optinova.entity.enums.PaymentStatus;
import com.optinova.entity.enums.Role;
import com.optinova.exception.BadRequestException;
import com.optinova.exception.ResourceNotFoundException;
import com.optinova.mapper.OrderMapper;
import com.optinova.repository.CartItemRepository;
import com.optinova.repository.OrderRepository;
import com.optinova.repository.ProductRepository;
import com.optinova.repository.UserRepository;
import com.optinova.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation managing order creation from active cart sessions, inventory adjustments, and status tracking.
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDto createOrder(Long userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cannot place order. Your shopping cart is empty.");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .user(user)
                .shippingAddress(request.getShippingAddress())
                .paymentMethod(request.getPaymentMethod())
                .orderStatus(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }

            // Deduct product inventory stock
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            BigDecimal itemPrice = (product.getDiscountPrice() != null &&
                    product.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0)
                    ? product.getDiscountPrice()
                    : product.getPrice();

            BigDecimal subtotal = itemPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(itemPrice)
                    .subtotal(subtotal)
                    .build();

            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        // Clear active cart session
        cartItemRepository.deleteByUserId(userId);

        return orderMapper.toOrderDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getUserOrders(Long userId) {
        verifyUserExists(userId);
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(orderMapper::toOrderDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long userId, Long orderId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        // Enforce data privacy: User can only view their own orders unless they possess ROLE_ADMIN
        if (!order.getUser().getId().equals(userId) && !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new BadRequestException("Unauthorized access to order details.");
        }

        return orderMapper.toOrderDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderDto> getAllOrders(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Order> orderPage = orderRepository.findAll(pageable);

        List<OrderDto> content = orderPage.getContent().stream()
                .map(orderMapper::toOrderDto)
                .collect(Collectors.toList());

        return PageResponse.<OrderDto>builder()
                .content(content)
                .pageNo(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .last(orderPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        order.setOrderStatus(request.getOrderStatus());
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toOrderDto(updatedOrder);
    }

    @Override
    @Transactional
    public OrderDto updatePaymentStatus(Long orderId, UpdatePaymentStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        order.setPaymentStatus(request.getPaymentStatus());
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toOrderDto(updatedOrder);
    }

    private void verifyUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
    }
}
