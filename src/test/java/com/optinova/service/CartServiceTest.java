package com.optinova.service;

import com.optinova.dto.AddToCartRequest;
import com.optinova.dto.CartResponse;
import com.optinova.dto.UpdateCartItemRequest;
import com.optinova.entity.CartItem;
import com.optinova.entity.Product;
import com.optinova.entity.User;
import com.optinova.exception.BadRequestException;
import com.optinova.exception.ResourceNotFoundException;
import com.optinova.mapper.CartMapper;
import com.optinova.repository.CartItemRepository;
import com.optinova.repository.ProductRepository;
import com.optinova.repository.UserRepository;
import com.optinova.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private CartMapper cartMapper = new CartMapper();

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Product product;
    private CartItem cartItem;
    private AddToCartRequest addToCartRequest;
    private UpdateCartItemRequest updateCartItemRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("user@example.com")
                .build();

        product = Product.builder()
                .id(10L)
                .name("Polarized Glasses")
                .price(new BigDecimal("100.00"))
                .stockQuantity(15)
                .isActive(true)
                .build();

        cartItem = CartItem.builder()
                .id(100L)
                .user(user)
                .product(product)
                .quantity(2)
                .totalPrice(new BigDecimal("200.00"))
                .build();

        addToCartRequest = AddToCartRequest.builder()
                .productId(10L)
                .quantity(2)
                .build();

        updateCartItemRequest = UpdateCartItemRequest.builder()
                .quantity(3)
                .build();
    }

    @Test
    @DisplayName("Should Retrieve User Cart Successfully")
    void testGetUserCartSuccess() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));

        CartResponse response = cartService.getUserCart(1L);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(2, response.getTotalItems());
        assertEquals(new BigDecimal("200.00"), response.getGrandTotal());
    }

    @Test
    @DisplayName("Should Add Item To Cart Successfully")
    void testAddItemToCartSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByUserIdAndProductId(1L, 10L)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));

        CartResponse response = cartService.addItemToCart(1L, addToCartRequest);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should Throw BadRequestException when Requested Quantity Exceeds Stock")
    void testAddItemInsufficientStock() {
        product.setStockQuantity(1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        assertThrows(BadRequestException.class, () -> cartService.addItemToCart(1L, addToCartRequest));
    }

    @Test
    @DisplayName("Should Throw ResourceNotFoundException when User ID Invalid")
    void testGetUserCartUserNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> cartService.getUserCart(99L));
    }
}
