package com.optinova.service;

import com.optinova.dto.ApiResponse;
import com.optinova.dto.ProductImageDto;
import com.optinova.dto.ProductImageRequest;

import java.util.List;

/**
 * Service interface defining Product Gallery Image management contracts.
 */
public interface ProductImageService {

    List<ProductImageDto> getImagesByProductId(Long productId);

    ProductImageDto addImageToProduct(Long productId, ProductImageRequest request);

    ProductImageDto updateProductImage(Long imageId, ProductImageRequest request);

    ApiResponse<String> deleteProductImage(Long imageId);
}
