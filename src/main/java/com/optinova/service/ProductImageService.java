package com.optinova.service;

import com.optinova.dto.ApiResponse;
import com.optinova.dto.ProductImageDto;
import com.optinova.dto.ProductImageRequest;

import java.util.List;

/**
 * Service interface defining Product Gallery Image management contracts.
 */
public interface ProductImageService {

    List<ProductImageDto> getImagesByProductId(Integer productId);

    ProductImageDto addImageToProduct(Integer productId, ProductImageRequest request);

    ProductImageDto updateProductImage(Integer imageId, ProductImageRequest request);

    ApiResponse<String> deleteProductImage(Integer imageId);
}
