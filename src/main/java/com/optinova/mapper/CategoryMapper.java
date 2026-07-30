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
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .active(category.isActive())
                .createdAt(category.getCreatedAt())
                .build();
    }

    public Category toEntity(CategoryRequest request) {
        if (request == null) {
            return null;
        }
        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .active(request.isActive())
                .build();
    }

    public void updateEntityFromRequest(Category category, CategoryRequest request) {
        if (category == null || request == null) {
            return;
        }
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setActive(request.isActive());
    }
}
