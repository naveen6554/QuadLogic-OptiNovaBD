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
 * Service implementation managing product image gallery resources.
 */
@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ProductImageMapper productImageMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductImageDto> getImagesByProductId(Integer productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        return productImageRepository.findByProductProductId(productId).stream()
                .map(productImageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductImageDto addImageToProduct(Integer productId, ProductImageRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        ProductImage productImage = productImageMapper.toEntity(request, product);
        ProductImage savedImage = productImageRepository.save(productImage);
        return productImageMapper.toDto(savedImage);
    }

    @Override
    @Transactional
    public ProductImageDto updateProductImage(Integer imageId, ProductImageRequest request) {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductImage", "id", imageId));

        productImage.setImageUrl(request.getImageUrl());

        ProductImage updatedImage = productImageRepository.save(productImage);
        return productImageMapper.toDto(updatedImage);
    }

    @Override
    @Transactional
    public ApiResponse<String> deleteProductImage(Integer imageId) {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductImage", "id", imageId));

        productImageRepository.delete(productImage);
        return ApiResponse.success("Product image with ID " + imageId + " deleted successfully.");
    }
}
