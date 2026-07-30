package com.optinova.service;

import com.optinova.dto.ApiResponse;
import com.optinova.dto.PageResponse;
import com.optinova.dto.ProductDto;
import com.optinova.dto.ProductRequest;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface defining Product management, search, filtering, and pagination contracts.
 */
public interface ProductService {

    PageResponse<ProductDto> getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir);

    ProductDto getProductById(Long id);

    PageResponse<ProductDto> searchProducts(String keyword, int pageNo, int pageSize, String sortBy, String sortDir);

    PageResponse<ProductDto> getProductsByCategory(Long categoryId, int pageNo, int pageSize, String sortBy, String sortDir);

    PageResponse<ProductDto> filterProductsByPrice(BigDecimal minPrice, BigDecimal maxPrice, int pageNo, int pageSize, String sortBy, String sortDir);

    List<ProductDto> getLatestProducts();

    List<ProductDto> getFeaturedProducts();

    ProductDto createProduct(ProductRequest productRequest);

    ProductDto updateProduct(Long id, ProductRequest productRequest);

    ApiResponse<String> deleteProduct(Long id);
}
