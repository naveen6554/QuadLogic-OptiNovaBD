package com.optinova.service.impl;

import com.optinova.dto.ApiResponse;
import com.optinova.dto.PageResponse;
import com.optinova.dto.ProductDto;
import com.optinova.dto.ProductRequest;
import com.optinova.entity.Category;
import com.optinova.entity.Product;
import com.optinova.entity.ProductImage;
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
        if (productRepository.count() == 0) {
            Category prescription = categoryRepository.findByCategoryName("Prescription Glasses")
                    .orElseGet(() -> categoryRepository.save(Category.builder().categoryName("Prescription Glasses").build()));

            Category sunglasses = categoryRepository.findByCategoryName("Sunglasses")
                    .orElseGet(() -> categoryRepository.save(Category.builder().categoryName("Sunglasses").build()));

            Category blueLight = categoryRepository.findByCategoryName("Blue Light Blocking")
                    .orElseGet(() -> categoryRepository.save(Category.builder().categoryName("Blue Light Blocking").build()));

            Category reading = categoryRepository.findByCategoryName("Reading Glasses")
                    .orElseGet(() -> categoryRepository.save(Category.builder().categoryName("Reading Glasses").build()));

            createDefaultProduct("Warby Parker Precision Frames", "Custom hand-polished acetate prescription eyewear with anti-scratch coating.", new BigDecimal("195.00"), new BigDecimal("80.00"), 50, prescription, "https://images.unsplash.com/photo-1572635196237-14b3f281503f?auto=format&fit=crop&w=800&q=80");
            createDefaultProduct("Zenni Optical Ultra-Light Aviator", "Flexible stainless steel aviator frame with polarized UV400 protective lenses.", new BigDecimal("89.99"), new BigDecimal("35.00"), 75, sunglasses, "https://images.unsplash.com/photo-1511499767150-a48a237f0083?auto=format&fit=crop&w=800&q=80");
            createDefaultProduct("TruVision Readers Magnifier", "Ergonomic lightweight reading glasses featuring precision anti-reflective focal lenses.", new BigDecimal("49.50"), new BigDecimal("18.00"), 100, reading, "https://images.unsplash.com/photo-1508296695146-257a814070b4?auto=format&fit=crop&w=800&q=80");
            createDefaultProduct("ThinOptics Keychain Blue Cut", "Ultra-compact shatterproof blue light blocking glasses with integrated slim travel case.", new BigDecimal("39.95"), new BigDecimal("12.00"), 120, blueLight, "https://images.unsplash.com/photo-1591076482161-42ce6da69f67?auto=format&fit=crop&w=800&q=80");
            createDefaultProduct("OptiNova Titan Precision Eyewear", "Ultra-lightweight Japanese Beta-Titanium frame with anti-reflective ZEISS precision optics.", new BigDecimal("249.99"), new BigDecimal("120.00"), 45, prescription, "https://images.unsplash.com/photo-1577803645773-f96470509666?auto=format&fit=crop&w=800&q=80");
        }
    }

    private void createDefaultProduct(String name, String desc, BigDecimal price, BigDecimal costPrice, int stock, Category category, String imageUrl) {
        Product product = Product.builder()
                .name(name)
                .description(desc)
                .price(price)
                .costPrice(costPrice)
                .stock(stock)
                .category(category)
                .build();

        ProductImage img = ProductImage.builder()
                .imageUrl(imageUrl)
                .product(product)
                .build();

        product.getImages().add(img);
        productRepository.save(product);
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
