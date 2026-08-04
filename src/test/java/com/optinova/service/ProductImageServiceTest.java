package com.optinova.service;

import com.optinova.dto.ProductImageDto;
import com.optinova.dto.ProductImageRequest;
import com.optinova.entity.Product;
import com.optinova.entity.ProductImage;
import com.optinova.exception.ResourceNotFoundException;
import com.optinova.mapper.ProductImageMapper;
import com.optinova.repository.ProductImageRepository;
import com.optinova.repository.ProductRepository;
import com.optinova.service.impl.ProductImageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductImageServiceTest {

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private ProductRepository productRepository;

    @Spy
    private ProductImageMapper productImageMapper = new ProductImageMapper();

    @InjectMocks
    private ProductImageServiceImpl productImageService;

    private Product product;
    private ProductImage productImage;
    private ProductImageRequest imageRequest;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .productId(1)
                .name("Wayfarer Black")
                .build();

        productImage = ProductImage.builder()
                .imageId(100)
                .imageUrl("https://optinova.com/images/wayfarer_1.jpg")
                .product(product)
                .build();

        imageRequest = ProductImageRequest.builder()
                .imageUrl("https://optinova.com/images/wayfarer_1.jpg")
                .build();
    }

    @Test
    @DisplayName("Should Get Images By Product ID Successfully")
    void testGetImagesByProductIdSuccess() {
        when(productRepository.existsById(1)).thenReturn(true);
        when(productImageRepository.findByProductProductId(1)).thenReturn(List.of(productImage));

        List<ProductImageDto> images = productImageService.getImagesByProductId(1);

        assertFalse(images.isEmpty());
        assertEquals(1, images.size());
        assertEquals("https://optinova.com/images/wayfarer_1.jpg", images.get(0).getImageUrl());
    }

    @Test
    @DisplayName("Should Add Image To Product Successfully")
    void testAddImageToProductSuccess() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productImageRepository.save(any(ProductImage.class))).thenReturn(productImage);

        ProductImageDto dto = productImageService.addImageToProduct(1, imageRequest);

        assertNotNull(dto);
        assertEquals("https://optinova.com/images/wayfarer_1.jpg", dto.getImageUrl());
        verify(productImageRepository, times(1)).save(any(ProductImage.class));
    }

    @Test
    @DisplayName("Should Throw ResourceNotFoundException when Product Not Found")
    void testAddImageProductNotFound() {
        when(productRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productImageService.addImageToProduct(99, imageRequest));
    }
}
