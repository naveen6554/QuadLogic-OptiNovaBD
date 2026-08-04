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
                .imageId(productImage.getImageId())
                .imageUrl(productImage.getImageUrl())
                .productId(productImage.getProduct() != null ? productImage.getProduct().getProductId() : null)
                .build();
    }

    public ProductImage toEntity(ProductImageRequest request, Product product) {
        if (request == null) {
            return null;
        }
        return ProductImage.builder()
                .imageUrl(request.getImageUrl())
                .product(product)
                .build();
    }
}
