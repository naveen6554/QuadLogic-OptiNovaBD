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
 * Service implementation managing product search, category filtering, pricing, and catalogue pagination.
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public PageResponse<ProductDto> getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir) {
        ensureProductsExist();
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Product> productPage = productRepository.findAll(pageable);
        return buildPageResponse(productPage);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return productMapper.toDto(product);
    }

    @Override
    @Transactional
    public PageResponse<ProductDto> searchProducts(String keyword, int pageNo, int pageSize, String sortBy, String sortDir) {
        ensureProductsExist();
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Product> productPage = productRepository.searchProducts(keyword, pageable);
        return buildPageResponse(productPage);
    }

    @Override
    @Transactional
    public PageResponse<ProductDto> getProductsByCategory(Integer categoryId, int pageNo, int pageSize, String sortBy, String sortDir) {
        ensureProductsExist();
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Product> productPage = productRepository.findByCategoryCategoryId(categoryId, pageable);
        return buildPageResponse(productPage);
    }

    @Override
    @Transactional
    public PageResponse<ProductDto> filterProductsByPrice(BigDecimal minPrice, BigDecimal maxPrice, int pageNo, int pageSize, String sortBy, String sortDir) {
        ensureProductsExist();
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Product> productPage = productRepository.filterByPriceRange(minPrice, maxPrice, pageable);
        return buildPageResponse(productPage);
    }

    @Override
    @Transactional
    public ProductDto createProduct(ProductRequest productRequest) {
        Category category = null;
        if (productRequest.getCategoryId() != null) {
            category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productRequest.getCategoryId()));
        }

        Product product = productMapper.toEntity(productRequest, category);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Integer id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        Category category = null;
        if (productRequest.getCategoryId() != null) {
            category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productRequest.getCategoryId()));
        }

        productMapper.updateEntityFromRequest(product, productRequest, category);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }

    @Override
    @Transactional
    public ApiResponse<String> deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        productRepository.delete(product);
        return ApiResponse.success("Product with ID " + id + " deleted successfully.");
    }

    private void ensureProductsExist() {
        // DataSeeder populates all 87 products on startup
    }

    private Pageable createPageable(int pageNo, int pageSize, String sortBy, String sortDir) {
        String effectiveSortBy = ("id".equalsIgnoreCase(sortBy) || sortBy == null || sortBy.isBlank()) ? "productId" : sortBy;
        Sort sort = sortDir != null && sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(effectiveSortBy).ascending() : Sort.by(effectiveSortBy).descending();
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
