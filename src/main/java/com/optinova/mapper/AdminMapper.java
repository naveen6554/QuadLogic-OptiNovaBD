package com.optinova.mapper;

import com.optinova.dto.AdminProductResponse;
import com.optinova.dto.AdminUserResponse;
import com.optinova.entity.Product;
import com.optinova.entity.User;
import org.springframework.stereotype.Component;

/**
 * Mapper utility component mapping entities to Admin DTOs.
 */
@Component
public class AdminMapper {

    /**
     * Converts Product JPA entity to AdminProductResponse DTO.
     *
     * @param product Entity instance
     * @return AdminProductResponse instance
     */
    public AdminProductResponse toProductResponse(Product product) {
        if (product == null) {
            return null;
        }

        java.util.List<String> imageUrls = (product.getImages() != null)
                ? product.getImages().stream().map(com.optinova.entity.ProductImage::getImageUrl).collect(java.util.stream.Collectors.toList())
                : java.util.Collections.emptyList();

        return AdminProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryId(product.getCategory() != null ? product.getCategory().getCategoryId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getCategoryName() : null)
                .imageUrls(imageUrls)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    /**
     * Converts User JPA entity to AdminUserResponse DTO.
     *
     * @param user Entity instance
     * @return AdminUserResponse instance
     */
    public AdminUserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        return AdminUserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
