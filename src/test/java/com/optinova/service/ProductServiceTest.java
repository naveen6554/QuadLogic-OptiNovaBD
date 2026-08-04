package com.optinova.service;

import com.optinova.dto.PageResponse;
import com.optinova.dto.ProductDto;
import com.optinova.dto.ProductRequest;
import com.optinova.entity.Category;
import com.optinova.entity.Product;
import com.optinova.exception.ResourceNotFoundException;
import com.optinova.mapper.CategoryMapper;
import com.optinova.mapper.ProductMapper;
import com.optinova.repository.CategoryRepository;
import com.optinova.repository.ProductRepository;
import com.optinova.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Spy
    private CategoryMapper categoryMapper = new CategoryMapper();

    @Spy
    private ProductMapper productMapper = new ProductMapper(new CategoryMapper());

    @InjectMocks
    private ProductServiceImpl productService;

    private Category category;
    private Product product;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .categoryId(1)
                .categoryName("Sunglasses")
                .build();

        product = Product.builder()
                .productId(10)
                .name("Aviator Classic Gold")
                .price(new BigDecimal("150.00"))
                .stock(25)
                .category(category)
                .build();

        productRequest = ProductRequest.builder()
                .name("Aviator Classic Gold")
                .price(new BigDecimal("150.00"))
                .stock(25)
                .categoryId(1)
                .build();
    }

    @Test
    @DisplayName("Should Retrieve Paginated List of All Products")
    void testGetAllProducts() {
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        PageResponse<ProductDto> response = productService.getAllProducts(0, 10, "productId", "asc");

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("Aviator Classic Gold", response.getContent().get(0).getName());
    }

    @Test
    @DisplayName("Should Get Product By ID Successfully")
    void testGetProductByIdSuccess() {
        when(productRepository.findById(10)).thenReturn(Optional.of(product));

        ProductDto dto = productService.getProductById(10);

        assertNotNull(dto);
        assertEquals(Integer.valueOf(10), dto.getId());
        assertEquals("Aviator Classic Gold", dto.getName());
    }

    @Test
    @DisplayName("Should Search Products by Keyword")
    void testSearchProducts() {
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.searchProducts(eq("Aviator"), any(Pageable.class))).thenReturn(page);

        PageResponse<ProductDto> response = productService.searchProducts("Aviator", 0, 10, "productId", "asc");

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("Aviator Classic Gold", response.getContent().get(0).getName());
    }

    @Test
    @DisplayName("Should Create Product Successfully")
    void testCreateProductSuccess() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto created = productService.createProduct(productRequest);

        assertNotNull(created);
        assertEquals("Aviator Classic Gold", created.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should Throw ResourceNotFoundException when Category ID Invalid during Product Creation")
    void testCreateProductCategoryNotFound() {
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());
        productRequest.setCategoryId(99);

        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(productRequest));
    }
}
