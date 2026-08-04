package com.optinova.service;

import com.optinova.dto.ApiResponse;
import com.optinova.dto.CategoryDto;
import com.optinova.dto.CategoryRequest;

import java.util.List;

/**
 * Service interface defining Category management CRUD contracts.
 */
public interface CategoryService {

    List<CategoryDto> getAllCategories();

    CategoryDto getCategoryById(Integer id);

    CategoryDto createCategory(CategoryRequest categoryRequest);

    CategoryDto updateCategory(Integer id, CategoryRequest categoryRequest);

    ApiResponse<String> deleteCategory(Integer id);
}
