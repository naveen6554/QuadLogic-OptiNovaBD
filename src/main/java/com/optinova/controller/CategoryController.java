package com.optinova.controller;

import com.optinova.constants.AppConstants;
import com.optinova.dto.ApiResponse;
import com.optinova.dto.CategoryDto;
import com.optinova.dto.CategoryRequest;
import com.optinova.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing Product Category management APIs.
 */
@RestController
@RequestMapping(AppConstants.CATEGORY_BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Category Module", description = "REST APIs for Product Category Management")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get All Categories", description = "Retrieves a list of all product categories.")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("Fetched categories successfully", categories));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Category By Id", description = "Retrieves category details by category ID.")
    public ResponseEntity<ApiResponse<CategoryDto>> getCategoryById(@PathVariable Integer id) {
        CategoryDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", category));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Category (Admin)", description = "Creates a new product category. Requires ADMIN role.")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        CategoryDto createdCategory = categoryService.createCategory(categoryRequest);
        return new ResponseEntity<>(ApiResponse.success("Category created successfully", createdCategory), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Category (Admin)", description = "Updates an existing product category by ID. Requires ADMIN role.")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(@PathVariable Integer id,
                                                                    @Valid @RequestBody CategoryRequest categoryRequest) {
        CategoryDto updatedCategory = categoryService.updateCategory(id, categoryRequest);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", updatedCategory));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete Category (Admin)", description = "Deletes a category by ID. Requires ADMIN role.")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Integer id) {
        ApiResponse<String> response = categoryService.deleteCategory(id);
        return ResponseEntity.ok(response);
    }
}
