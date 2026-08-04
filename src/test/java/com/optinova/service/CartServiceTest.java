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
                .userId(1)
                .email("user@example.com")
                .build();

        product = Product.builder()
                .productId(10)
                .name("Polarized Glasses")
                .price(new BigDecimal("100.00"))
                .stock(15)
                .build();

        cartItem = CartItem.builder()
                .id(100)
                .user(user)
                .product(product)
                .quantity(2)
                .build();

        addToCartRequest = AddToCartRequest.builder()
                .productId(10)
                .quantity(2)
                .build();

        updateCartItemRequest = UpdateCartItemRequest.builder()
                .quantity(3)
                .build();
    }

    @Test
    @DisplayName("Should Retrieve User Cart Successfully")
    void testGetUserCartSuccess() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(cartItemRepository.findByUserUserId(1)).thenReturn(List.of(cartItem));

        CartResponse response = cartService.getUserCart(1);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(2, response.getTotalItems());
        assertEquals(new BigDecimal("200.00"), response.getGrandTotal());
    }

    @Test
    @DisplayName("Should Add Item To Cart Successfully")
    void testAddItemToCartSuccess() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(10)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByUserUserIdAndProductProductId(1, 10)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartItemRepository.findByUserUserId(1)).thenReturn(List.of(cartItem));

        CartResponse response = cartService.addItemToCart(1, addToCartRequest);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should Throw BadRequestException when Requested Quantity Exceeds Stock")
    void testAddItemInsufficientStock() {
        product.setStock(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(10)).thenReturn(Optional.of(product));

        assertThrows(BadRequestException.class, () -> cartService.addItemToCart(1, addToCartRequest));
    }

    @Test
    @DisplayName("Should Update Cart Item Quantity Successfully")
    void testUpdateCartItemQuantitySuccess() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(cartItemRepository.findById(100)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartItemRepository.findByUserUserId(1)).thenReturn(List.of(cartItem));

        CartResponse response = cartService.updateCartItemQuantity(1, 100, updateCartItemRequest);

        assertNotNull(response);
        verify(cartItemRepository, times(1)).save(cartItem);
    }

    @Test
    @DisplayName("Should Throw ResourceNotFoundException when User ID Invalid")
    void testGetUserCartUserNotFound() {
        when(userRepository.existsById(99)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> cartService.getUserCart(99));
    }
}
