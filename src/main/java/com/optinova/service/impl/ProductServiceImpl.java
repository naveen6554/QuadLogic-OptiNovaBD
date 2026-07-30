package com.optinova.service.impl;

import com.optinova.dto.ApiResponse;
import com.optinova.dto.PageResponse;
import com.optinova.dto.ProductDto;
import com.optinova.dto.ProductRequest;
import com.optinova.entity.Category;
import com.optinova.entity.Product;
import com.optinova.exception.ResourceNotFoundException;
import com.optinova.mapper.ProductMapper;
import com.optinova.repository.CategoryRepository;
import com.optinova.repository.ProductRepository;
import com.optinova.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation managing optical product search, category filtering, pricing, and catalogue pagination.
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductDto> getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Product> productPage = productRepository.findByIsActiveTrue(pageable);
        return buildPageResponse(productPage);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return productMapper.toDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductDto> searchProducts(String keyword, int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Product> productPage = productRepository.searchProducts(keyword, pageable);
        return buildPageResponse(productPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductDto> getProductsByCategory(Long categoryId, int pageNo, int pageSize, String sortBy, String sortDir) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Product> productPage = productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable);
        return buildPageResponse(productPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductDto> filterProductsByPrice(BigDecimal minPrice, BigDecimal maxPrice, int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Product> productPage = productRepository.filterByPriceRange(minPrice, maxPrice, pageable);
        return buildPageResponse(productPage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getLatestProducts() {
        return productRepository.findTop10ByIsActiveTrueOrderByCreatedAtDesc().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getFeaturedProducts() {
        return productRepository.findByIsFeaturedTrueAndIsActiveTrue().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductDto createProduct(ProductRequest productRequest) {
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productRequest.getCategoryId()));

        Product product = productMapper.toEntity(productRequest, category);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productRequest.getCategoryId()));

        productMapper.updateEntityFromRequest(product, productRequest, category);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }

    @Override
    @Transactional
    public ApiResponse<String> deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        productRepository.delete(product);
        return ApiResponse.success("Product with ID " + id + " deleted successfully.");
    }

    private Pageable createPageable(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(pageNo, pageSize, sort);
    }

    private PageResponse<ProductDto> buildPageResponse(Page<Product> productPage) {
        List<ProductDto> content = productPage.getContent().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());

        return PageResponse.<ProductDto>builder()
                .content(content)
                .pageNo(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .last(productPage.isLast())
                .build();
    }
}
