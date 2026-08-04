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

import java.util.List;
import java.util.Optional;

/**
 * Service implementation managing user shopping cart items.
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
    public CartResponse getUserCart(Integer userId) {
        verifyUserExists(userId);
        List<CartItem> cartItems = cartItemRepository.findByUserUserId(userId);
        return cartMapper.toCartResponse(cartItems);
    }

    @Override
    @Transactional
    public CartResponse addItemToCart(Integer userId, AddToCartRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        if (product.getStockQuantity() == null || product.getStockQuantity() <= 0) {
            throw new BadRequestException("Product is out of stock.");
        }

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new BadRequestException("Insufficient stock available. Requested: "
                    + request.getQuantity() + ", Available: " + product.getStockQuantity());
        }

        Optional<CartItem> existingCartItem = cartItemRepository.findByUserUserIdAndProductProductId(userId, product.getProductId());

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + request.getQuantity();

            if (product.getStockQuantity() < newQuantity) {
                throw new BadRequestException("Cannot add requested quantity. Exceeds total available stock (" + product.getStockQuantity() + ").");
            }

            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        } else {
            CartItem newCartItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newCartItem);
        }

        List<CartItem> updatedCart = cartItemRepository.findByUserUserId(userId);
        return cartMapper.toCartResponse(updatedCart);
    }

    @Override
    @Transactional
    public CartResponse updateCartItemQuantity(Integer userId, Integer cartItemId, UpdateCartItemRequest request) {
        verifyUserExists(userId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        if (!cartItem.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("Unauthorized access to cart item.");
        }

        Product product = cartItem.getProduct();
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new BadRequestException("Requested quantity exceeds available stock (" + product.getStockQuantity() + ").");
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        List<CartItem> updatedCart = cartItemRepository.findByUserUserId(userId);
        return cartMapper.toCartResponse(updatedCart);
    }

    @Override
    @Transactional
    public CartResponse removeCartItem(Integer userId, Integer cartItemId) {
        verifyUserExists(userId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        if (!cartItem.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("Unauthorized access to cart item.");
        }

        cartItemRepository.delete(cartItem);

        List<CartItem> updatedCart = cartItemRepository.findByUserUserId(userId);
        return cartMapper.toCartResponse(updatedCart);
    }

    @Override
    @Transactional
    public ApiResponse<String> clearUserCart(Integer userId) {
        verifyUserExists(userId);
        cartItemRepository.deleteByUserUserId(userId);
        return ApiResponse.success("Shopping cart cleared successfully.");
    }

    private void verifyUserExists(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
    }
}
