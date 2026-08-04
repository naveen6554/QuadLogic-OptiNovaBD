package com.optinova.service.impl;

import com.optinova.dto.*;
import com.optinova.entity.CartItem;
import com.optinova.entity.Order;
import com.optinova.entity.OrderItem;
import com.optinova.entity.Product;
import com.optinova.entity.User;
import com.optinova.entity.enums.OrderStatus;
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
import lombok.extern.slf4j.Slf4j;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation managing order creation from active cart sessions, inventory adjustments, and status tracking.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final com.optinova.repository.ProductImageRepository productImageRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDto createOrder(Integer userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<CartItem> cartItems = cartItemRepository.findByUserUserId(userId);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cannot place order. Your shopping cart is empty.");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        String generatedOrderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Order order = Order.builder()
                .orderId(generatedOrderId)
                .user(user)
                .status(OrderStatus.SUCCESS)
                .totalAmount(BigDecimal.ZERO)
                .build();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            BigDecimal itemPrice = product.getPrice();
            BigDecimal subtotal = itemPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .pricePerUnit(itemPrice)
                    .totalPrice(subtotal)
                    .build();

            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        cartItemRepository.deleteByUserUserId(userId);

        return orderMapper.toOrderDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getUserOrders(Integer userId) {
        verifyUserExists(userId);
        return orderRepository.findByUserUserIdOrderByCreatedAtDesc(userId).stream()
                .map(orderMapper::toOrderDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Integer userId, String orderId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getUser().getUserId().equals(userId) && !user.getRole().equals(Role.ADMIN)) {
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
    public OrderDto updateOrderStatus(String orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        order.setStatus(request.getOrderStatus());
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toOrderDto(updatedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public UserOrdersResponse getUserSuccessOrders(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<Order> successOrders = orderRepository.findUserSuccessOrdersWithDetails(userId, OrderStatus.SUCCESS);

        List<SuccessOrderProductDto> productDtos = new ArrayList<>();
        if (successOrders != null) {
            for (Order order : successOrders) {
                if (order.getOrderItems() != null) {
                    for (OrderItem item : order.getOrderItems()) {
                        Product product = item.getProduct();
                        String imageUrl = "";
                        if (product != null) {
                            if (product.getImages() != null && !product.getImages().isEmpty() && product.getImages().get(0).getImageUrl() != null) {
                                imageUrl = product.getImages().get(0).getImageUrl();
                            }
                            if ((imageUrl == null || imageUrl.isBlank()) && product.getProductId() != null) {
                                List<com.optinova.entity.ProductImage> pImages = productImageRepository.findByProductProductId(product.getProductId());
                                if (pImages != null && !pImages.isEmpty() && pImages.get(0).getImageUrl() != null) {
                                    imageUrl = pImages.get(0).getImageUrl();
                                }
                            }
                        }

                        SuccessOrderProductDto productDto = SuccessOrderProductDto.builder()
                                .orderId(order.getOrderId())
                                .productId(product != null ? product.getProductId() : null)
                                .name(product != null ? product.getName() : null)
                                .description(product != null ? product.getDescription() : null)
                                .category(product != null && product.getCategory() != null ? product.getCategory().getName() : null)
                                .quantity(item.getQuantity())
                                .pricePerUnit(item.getPricePerUnit())
                                .totalPrice(item.getTotalPrice())
                                .imageUrl(imageUrl)
                                .status(order.getStatus() != null ? order.getStatus().name() : OrderStatus.SUCCESS.name())
                                .orderDate(order.getCreatedAt() != null ? order.getCreatedAt().toString() : "")
                                .build();

                        log.info("Order Module -> OrderID: {}, ProductID: {}, Name: {}, DB Image URL: '{}'",
                                order.getOrderId(),
                                product != null ? product.getProductId() : null,
                                product != null ? product.getName() : null,
                                imageUrl);

                        productDtos.add(productDto);
                    }
                }
            }
        }

        String displayUsername = (user.getUsername() != null && !user.getUsername().isBlank()) ? user.getUsername() : user.getEmail();

        return UserOrdersResponse.builder()
                .role(user.getRole() != null ? user.getRole().name() : "CUSTOMER")
                .username(displayUsername)
                .orders(UserOrdersResponse.OrderProductsWrapper.builder()
                        .products(productDtos)
                        .build())
                .build();
    }

    private void verifyUserExists(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
    }
}
