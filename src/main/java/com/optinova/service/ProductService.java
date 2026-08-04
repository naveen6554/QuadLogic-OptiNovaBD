package com.optinova.service;

import com.optinova.dto.ApiResponse;
import com.optinova.dto.PageResponse;
import com.optinova.dto.ProductDto;
import com.optinova.dto.ProductRequest;

import java.math.BigDecimal;

/**
 * Service interface defining Product management, search, filtering, and pagination contracts.
 */
public interface ProductService {

    PageResponse<ProductDto> getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir);

    ProductDto getProductById(Integer id);

    PageResponse<ProductDto> searchProducts(String keyword, int pageNo, int pageSize, String sortBy, String sortDir);

    PageResponse<ProductDto> getProductsByCategory(Integer categoryId, int pageNo, int pageSize, String sortBy, String sortDir);

    PageResponse<ProductDto> filterProductsByPrice(BigDecimal minPrice, BigDecimal maxPrice, int pageNo, int pageSize, String sortBy, String sortDir);

    ProductDto createProduct(ProductRequest productRequest);

    ProductDto updateProduct(Integer id, ProductRequest productRequest);

    ApiResponse<String> deleteProduct(Integer id);
}
