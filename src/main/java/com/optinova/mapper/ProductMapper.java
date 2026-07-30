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

        String primaryImageUrl = (product.getImages() != null)
                ? product.getImages().stream()
                .filter(ProductImage::isPrimary)
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse(!imageUrls.isEmpty() ? imageUrls.get(0) : null)
                : null;

        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .brand(product.getBrand())
                .frameType(product.getFrameType())
                .frameShape(product.getFrameShape())
                .gender(product.getGender())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .stockQuantity(product.getStockQuantity())
                .isFeatured(product.isFeatured())
                .isActive(product.isActive())
                .category(categoryMapper.toDto(product.getCategory()))
                .primaryImageUrl(primaryImageUrl)
                .imageUrls(imageUrls)
                .createdAt(product.getCreatedAt())
                .build();
    }

    public Product toEntity(ProductRequest request, Category category) {
        if (request == null) {
            return null;
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .brand(request.getBrand())
                .frameType(request.getFrameType())
                .frameShape(request.getFrameShape())
                .gender(request.getGender())
                .price(request.getPrice())
                .discountPrice(request.getDiscountPrice())
                .stockQuantity(request.getStockQuantity())
                .isFeatured(request.isFeatured())
                .isActive(request.isActive())
                .category(category)
                .build();

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<ProductImage> images = request.getImageUrls().stream().map(url -> {
                boolean isFirst = request.getImageUrls().indexOf(url) == 0;
                return ProductImage.builder()
                        .imageUrl(url)
                        .isPrimary(isFirst)
                        .product(product)
                        .build();
            }).collect(Collectors.toList());
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
        product.setBrand(request.getBrand());
        product.setFrameType(request.getFrameType());
        product.setFrameShape(request.getFrameShape());
        product.setGender(request.getGender());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setFeatured(request.isFeatured());
        product.setActive(request.isActive());
        product.setCategory(category);
    }
}
