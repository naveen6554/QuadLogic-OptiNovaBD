package com.optinova.service.impl;

import com.optinova.dto.AddToCartRequest;
import com.optinova.dto.ApiResponse;
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
import com.optinova.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation managing user shopping cart items, item calculations, and stock checks.
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getUserCart(Long userId) {
        verifyUserExists(userId);
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        return cartMapper.toCartResponse(cartItems);
    }

    @Override
    @Transactional
    public CartResponse addItemToCart(Long userId, AddToCartRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        if (!product.isActive()) {
            throw new BadRequestException("Product is currently unavailable for purchase.");
        }

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new BadRequestException("Insufficient stock available. Requested: "
                    + request.getQuantity() + ", Available: " + product.getStockQuantity());
        }

        BigDecimal unitPrice = (product.getDiscountPrice() != null &&
                product.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0)
                ? product.getDiscountPrice()
                : product.getPrice();

        Optional<CartItem> existingCartItem = cartItemRepository.findByUserIdAndProductId(userId, product.getId());

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + request.getQuantity();

            if (product.getStockQuantity() < newQuantity) {
                throw new BadRequestException("Cannot add requested quantity. Exceeds total available stock.");
            }

            cartItem.setQuantity(newQuantity);
            cartItem.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(newQuantity)));
            cartItemRepository.save(cartItem);
        } else {
            CartItem newCartItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(request.getQuantity())
                    .totalPrice(unitPrice.multiply(BigDecimal.valueOf(request.getQuantity())))
                    .build();
            cartItemRepository.save(newCartItem);
        }

        List<CartItem> updatedCart = cartItemRepository.findByUserId(userId);
        return cartMapper.toCartResponse(updatedCart);
    }

    @Override
    @Transactional
    public CartResponse updateCartItemQuantity(Long userId, Long cartItemId, UpdateCartItemRequest request) {
        verifyUserExists(userId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        if (!cartItem.getUser().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized access to cart item.");
        }

        Product product = cartItem.getProduct();
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new BadRequestException("Requested quantity exceeds available stock (" + product.getStockQuantity() + ").");
        }

        BigDecimal unitPrice = (product.getDiscountPrice() != null &&
                product.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0)
                ? product.getDiscountPrice()
                : product.getPrice();

        cartItem.setQuantity(request.getQuantity());
        cartItem.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(request.getQuantity())));
        cartItemRepository.save(cartItem);

        List<CartItem> updatedCart = cartItemRepository.findByUserId(userId);
        return cartMapper.toCartResponse(updatedCart);
    }

    @Override
    @Transactional
    public CartResponse removeCartItem(Long userId, Long cartItemId) {
        verifyUserExists(userId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        if (!cartItem.getUser().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized access to cart item.");
        }

        cartItemRepository.delete(cartItem);

        List<CartItem> updatedCart = cartItemRepository.findByUserId(userId);
        return cartMapper.toCartResponse(updatedCart);
    }

    @Override
    @Transactional
    public ApiResponse<String> clearUserCart(Long userId) {
        verifyUserExists(userId);
        cartItemRepository.deleteByUserId(userId);
        return ApiResponse.success("Shopping cart cleared successfully.");
    }

    private void verifyUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
    }
}
