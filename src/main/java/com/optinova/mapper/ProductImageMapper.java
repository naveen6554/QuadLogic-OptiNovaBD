package com.optinova.mapper;

import com.optinova.dto.ProductImageDto;
import com.optinova.dto.ProductImageRequest;
import com.optinova.entity.Product;
import com.optinova.entity.ProductImage;
import org.springframework.stereotype.Component;

/**
 * Mapper component converting between ProductImage Entity and ProductImage DTOs.
 */
@Component
public class ProductImageMapper {

    public ProductImageDto toDto(ProductImage productImage) {
        if (productImage == null) {
            return null;
        }
        return ProductImageDto.builder()
                .id(productImage.getId())
                .imageUrl(productImage.getImageUrl())
                .isPrimary(productImage.isPrimary())
                .productId(productImage.getProduct() != null ? productImage.getProduct().getId() : null)
                .build();
    }

    public ProductImage toEntity(ProductImageRequest request, Product product) {
        if (request == null) {
            return null;
        }
        return ProductImage.builder()
                .imageUrl(request.getImageUrl())
                .isPrimary(request.isPrimary())
                .product(product)
                .build();
    }
}
