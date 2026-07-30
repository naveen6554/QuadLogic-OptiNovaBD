package com.optinova.service;

import com.optinova.dto.CategoryDto;
import com.optinova.dto.CategoryRequest;
import com.optinova.entity.Category;
import com.optinova.exception.DuplicateResourceException;
import com.optinova.exception.ResourceNotFoundException;
import com.optinova.mapper.CategoryMapper;
import com.optinova.repository.CategoryRepository;
import com.optinova.service.impl.CategoryServiceImpl;
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
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Spy
    private CategoryMapper categoryMapper = new CategoryMapper();

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryRequest categoryRequest;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Eyeglasses")
                .description("Optical frames and prescription lenses")
                .active(true)
                .build();

        categoryRequest = CategoryRequest.builder()
                .name("Eyeglasses")
                .description("Optical frames and prescription lenses")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should Retrieve All Categories")
    void testGetAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<CategoryDto> categories = categoryService.getAllCategories();

        assertFalse(categories.isEmpty());
        assertEquals(1, categories.size());
        assertEquals("Eyeglasses", categories.get(0).getName());
    }

    @Test
    @DisplayName("Should Get Category By Id Successfully")
    void testGetCategoryByIdSuccess() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryDto dto = categoryService.getCategoryById(1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Eyeglasses", dto.getName());
    }

    @Test
    @DisplayName("Should Throw ResourceNotFoundException when Category ID Not Found")
    void testGetCategoryByIdNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(99L));
    }

    @Test
    @DisplayName("Should Create Category Successfully")
    void testCreateCategorySuccess() {
        when(categoryRepository.existsByName("Eyeglasses")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto created = categoryService.createCategory(categoryRequest);

        assertNotNull(created);
        assertEquals("Eyeglasses", created.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Should Throw DuplicateResourceException when Category Name Exists")
    void testCreateCategoryDuplicateName() {
        when(categoryRepository.existsByName("Eyeglasses")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> categoryService.createCategory(categoryRequest));
        verify(categoryRepository, never()).save(any());
    }
}
