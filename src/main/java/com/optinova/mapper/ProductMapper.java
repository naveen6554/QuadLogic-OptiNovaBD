package com.optinova.mapper;

import com.optinova.dto.ProductDto;
import com.optinova.dto.ProductRequest;
import com.optinova.entity.Category;
import com.optinova.entity.Product;
import com.optinova.entity.ProductImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper component converting between Product Entity and Product DTOs.
 */
@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final CategoryMapper categoryMapper;

    public ProductDto toDto(Product product) {
        if (product == null) {
            return null;
        }

        List<String> imageUrls = (product.getImages() != null)
                ? product.getImages().stream().map(ProductImage::getImageUrl).collect(Collectors.toList())
                : Collections.emptyList();

        return ProductDto.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(categoryMapper.toDto(product.getCategory()))
                .imageUrls(imageUrls)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public Product toEntity(ProductRequest request, Category category) {
        if (request == null) {
            return null;
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(category)
                .build();

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<ProductImage> images = request.getImageUrls().stream().map(url -> ProductImage.builder()
                    .imageUrl(url)
                    .product(product)
                    .build()).collect(Collectors.toList());
            product.setImages(images);
        }

        return product;
    }

    public void updateEntityFromRequest(Product product, ProductRequest request, Category category) {
        if (product == null || request == null) {
            return;
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);
    }
}
