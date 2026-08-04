package com.optinova.service.impl;

import com.optinova.dto.ApiResponse;
import com.optinova.dto.CategoryDto;
import com.optinova.dto.CategoryRequest;
import com.optinova.entity.Category;
import com.optinova.exception.DuplicateResourceException;
import com.optinova.exception.ResourceNotFoundException;
import com.optinova.mapper.CategoryMapper;
import com.optinova.repository.CategoryRepository;
import com.optinova.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation managing product category business logic.
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryRequest categoryRequest) {
        if (categoryRepository.existsByCategoryName(categoryRequest.getCategoryName())) {
            throw new DuplicateResourceException("Category already exists with name: " + categoryRequest.getCategoryName());
        }

        Category category = categoryMapper.toEntity(categoryRequest);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Integer id, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (!category.getCategoryName().equalsIgnoreCase(categoryRequest.getCategoryName()) &&
                categoryRepository.existsByCategoryName(categoryRequest.getCategoryName())) {
            throw new DuplicateResourceException("Category name already exists: " + categoryRequest.getCategoryName());
        }

        categoryMapper.updateEntityFromRequest(category, categoryRequest);
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    @Transactional
    public ApiResponse<String> deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        categoryRepository.delete(category);
        return ApiResponse.success("Category with ID " + id + " deleted successfully.");
    }
}
