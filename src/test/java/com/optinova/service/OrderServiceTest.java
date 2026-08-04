package com.optinova.service;

import com.optinova.dto.CreateOrderRequest;
import com.optinova.dto.OrderDto;
import com.optinova.dto.UpdateOrderStatusRequest;
import com.optinova.entity.CartItem;
import com.optinova.entity.Order;
import com.optinova.entity.Product;
import com.optinova.entity.User;
import com.optinova.entity.enums.OrderStatus;
import com.optinova.entity.enums.Role;
import com.optinova.exception.BadRequestException;
import com.optinova.mapper.OrderMapper;
import com.optinova.repository.CartItemRepository;
import com.optinova.repository.OrderRepository;
import com.optinova.repository.ProductImageRepository;
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

    @Mock
    private ProductImageRepository productImageRepository;

    @Spy
    @InjectMocks
    private OrderMapper orderMapper;

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
                .userId(1)
                .email("john.doe@example.com")
                .role(Role.CUSTOMER)
                .build();

        product = Product.builder()
                .productId(10)
                .name("Computer Glasses")
                .price(new BigDecimal("50.00"))
                .stock(10)
                .build();

        cartItem = CartItem.builder()
                .id(100)
                .user(user)
                .product(product)
                .quantity(2)
                .build();

        order = Order.builder()
                .orderId("ORD-12345678")
                .user(user)
                .status(OrderStatus.PENDING)
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
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(cartItemRepository.findByUserUserId(1)).thenReturn(List.of(cartItem));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDto created = orderService.createOrder(1, createOrderRequest);

        assertNotNull(created);
        assertEquals("ORD-12345678", created.getOrderId());
        assertEquals(OrderStatus.PENDING, created.getStatus());
        verify(productRepository, times(1)).save(any(Product.class));
        verify(cartItemRepository, times(1)).deleteByUserUserId(1);
    }

    @Test
    @DisplayName("Should Throw BadRequestException when Cart is Empty")
    void testCreateOrderEmptyCart() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(cartItemRepository.findByUserUserId(1)).thenReturn(Collections.emptyList());

        assertThrows(BadRequestException.class, () -> orderService.createOrder(1, createOrderRequest));
    }

    @Test
    @DisplayName("Should Update Order Status Successfully")
    void testUpdateOrderStatusSuccess() {
        when(orderRepository.findById("ORD-12345678")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                .orderStatus(OrderStatus.SUCCESS)
                .build();

        OrderDto updated = orderService.updateOrderStatus("ORD-12345678", request);

        assertNotNull(updated);
        assertEquals(OrderStatus.SUCCESS, order.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should Get User Success Orders Successfully")
    void testGetUserSuccessOrdersSuccess() {
        Order successOrder = Order.builder()
                .orderId("ORD-99999999")
                .user(user)
                .status(OrderStatus.SUCCESS)
                .totalAmount(new BigDecimal("100.00"))
                .orderItems(List.of(
                        com.optinova.entity.OrderItem.builder()
                                .id(1)
                                .product(product)
                                .quantity(2)
                                .pricePerUnit(new BigDecimal("50.00"))
                                .totalPrice(new BigDecimal("100.00"))
                                .build()
                ))
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(orderRepository.findUserSuccessOrdersWithDetails(1, OrderStatus.SUCCESS)).thenReturn(List.of(successOrder));

        com.optinova.dto.UserOrdersResponse response = orderService.getUserSuccessOrders(1);

        assertNotNull(response);
        assertEquals("CUSTOMER", response.getRole());
        assertNotNull(response.getOrders());
        assertEquals(1, response.getOrders().getProducts().size());
        assertEquals("ORD-99999999", response.getOrders().getProducts().get(0).getOrderId());
        assertEquals("Computer Glasses", response.getOrders().getProducts().get(0).getName());
    }

    @Test
    @DisplayName("Should Return Empty Products Array When No Success Orders Exist")
    void testGetUserSuccessOrdersEmpty() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(orderRepository.findUserSuccessOrdersWithDetails(1, OrderStatus.SUCCESS)).thenReturn(Collections.emptyList());

        com.optinova.dto.UserOrdersResponse response = orderService.getUserSuccessOrders(1);

        assertNotNull(response);
        assertEquals("CUSTOMER", response.getRole());
        assertNotNull(response.getOrders());
        assertTrue(response.getOrders().getProducts().isEmpty());
    }
}
