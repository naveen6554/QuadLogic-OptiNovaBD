package com.optinova.service.impl;

import com.optinova.dto.ApiResponse;
import com.optinova.dto.ProductImageDto;
import com.optinova.dto.ProductImageRequest;
import com.optinova.entity.Product;
import com.optinova.entity.ProductImage;
import com.optinova.exception.ResourceNotFoundException;
import com.optinova.mapper.ProductImageMapper;
import com.optinova.repository.ProductImageRepository;
import com.optinova.repository.ProductRepository;
import com.optinova.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation managing optical product image gallery resources.
 */
@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ProductImageMapper productImageMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductImageDto> getImagesByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        return productImageRepository.findByProductId(productId).stream()
                .map(productImageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductImageDto addImageToProduct(Long productId, ProductImageRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // If the new image is set to primary, reset pre-existing primary images
        if (request.isPrimary()) {
            resetPrimaryFlags(productId);
        }

        ProductImage productImage = productImageMapper.toEntity(request, product);
        ProductImage savedImage = productImageRepository.save(productImage);
        return productImageMapper.toDto(savedImage);
    }

    @Override
    @Transactional
    public ProductImageDto updateProductImage(Long imageId, ProductImageRequest request) {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductImage", "id", imageId));

        if (request.isPrimary() && !productImage.isPrimary()) {
            resetPrimaryFlags(productImage.getProduct().getId());
        }

        productImage.setImageUrl(request.getImageUrl());
        productImage.setPrimary(request.isPrimary());

        ProductImage updatedImage = productImageRepository.save(productImage);
        return productImageMapper.toDto(updatedImage);
    }

    @Override
    @Transactional
    public ApiResponse<String> deleteProductImage(Long imageId) {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductImage", "id", imageId));

        productImageRepository.delete(productImage);
        return ApiResponse.success("Product image with ID " + imageId + " deleted successfully.");
    }

    private void resetPrimaryFlags(Long productId) {
        List<ProductImage> existingImages = productImageRepository.findByProductId(productId);
        existingImages.forEach(img -> {
            if (img.isPrimary()) {
                img.setPrimary(false);
            }
        });
        productImageRepository.saveAll(existingImages);
    }
}
