package com.optinova.service;

import com.optinova.dto.CreateOrderRequest;
import com.optinova.dto.OrderDto;
import com.optinova.dto.UpdateOrderStatusRequest;
import com.optinova.entity.CartItem;
import com.optinova.entity.Order;
import com.optinova.entity.Product;
import com.optinova.entity.User;
import com.optinova.entity.enums.OrderStatus;
import com.optinova.entity.enums.PaymentStatus;
import com.optinova.entity.enums.Role;
import com.optinova.exception.BadRequestException;
import com.optinova.mapper.OrderMapper;
import com.optinova.repository.CartItemRepository;
import com.optinova.repository.OrderRepository;
import com.optinova.repository.ProductRepository;
import com.optinova.repository.UserRepository;
import com.optinova.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Spy
    private OrderMapper orderMapper = new OrderMapper();

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Product product;
    private CartItem cartItem;
    private Order order;
    private CreateOrderRequest createOrderRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("john.doe@example.com")
                .role(Role.ROLE_USER)
                .build();

        product = Product.builder()
                .id(10L)
                .name("Computer Glasses")
                .price(new BigDecimal("50.00"))
                .stockQuantity(10)
                .isActive(true)
                .build();

        cartItem = CartItem.builder()
                .id(100L)
                .user(user)
                .product(product)
                .quantity(2)
                .totalPrice(new BigDecimal("100.00"))
                .build();

        order = Order.builder()
                .id(500L)
                .orderNumber("ORD-12345678")
                .user(user)
                .shippingAddress("123 Optical St, Vision City")
                .paymentMethod("CREDIT_CARD")
                .orderStatus(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .totalAmount(new BigDecimal("100.00"))
                .build();

        createOrderRequest = CreateOrderRequest.builder()
                .shippingAddress("123 Optical St, Vision City")
                .paymentMethod("CREDIT_CARD")
                .build();
    }

    @Test
    @DisplayName("Should Create Order Successfully from Cart")
    void testCreateOrderSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDto created = orderService.createOrder(1L, createOrderRequest);

        assertNotNull(created);
        assertEquals("ORD-12345678", created.getOrderNumber());
        assertEquals(OrderStatus.PENDING, created.getOrderStatus());
        verify(productRepository, times(1)).save(any(Product.class));
        verify(cartItemRepository, times(1)).deleteByUserId(1L);
    }

    @Test
    @DisplayName("Should Throw BadRequestException when Cart is Empty")
    void testCreateOrderEmptyCart() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartItemRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        assertThrows(BadRequestException.class, () -> orderService.createOrder(1L, createOrderRequest));
    }

    @Test
    @DisplayName("Should Update Order Status Successfully")
    void testUpdateOrderStatusSuccess() {
        when(orderRepository.findById(500L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                .orderStatus(OrderStatus.SHIPPED)
                .build();

        OrderDto updated = orderService.updateOrderStatus(500L, request);

        assertNotNull(updated);
        assertEquals(OrderStatus.SHIPPED, order.getOrderStatus());
        verify(orderRepository, times(1)).save(order);
    }
}
