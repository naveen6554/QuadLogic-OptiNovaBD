package com.optinova.mapper;

import com.optinova.dto.CategoryDto;
import com.optinova.dto.CategoryRequest;
import com.optinova.entity.Category;
import org.springframework.stereotype.Component;

/**
 * Mapper component converting between Category Entity and Category DTOs.
 */
@Component
public class CategoryMapper {

    public CategoryDto toDto(Category category) {
        if (category == null) {
            return null;
        }
        return CategoryDto.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .build();
    }

    public Category toEntity(CategoryRequest request) {
        if (request == null) {
            return null;
        }
        return Category.builder()
                .categoryName(request.getCategoryName())
                .build();
    }

    public void updateEntityFromRequest(Category category, CategoryRequest request) {
        if (category == null || request == null) {
            return;
        }
        category.setCategoryName(request.getCategoryName());
    }
}
