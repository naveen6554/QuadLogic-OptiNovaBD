package com.optinova.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing category details in API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {

    private Integer categoryId;
    private String categoryName;

    public Integer getId() {
        return categoryId;
    }

    public String getName() {
        return categoryName;
    }
}
