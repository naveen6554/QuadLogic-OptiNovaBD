package com.optinova.controller;

import com.optinova.constants.AppConstants;
import com.optinova.dto.ApiResponse;
import com.optinova.dto.PageResponse;
import com.optinova.dto.ProductDto;
import com.optinova.dto.ProductRequest;
import com.optinova.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * REST Controller exposing Product Catalogue APIs.
 */
@RestController
@RequestMapping(AppConstants.PRODUCT_BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Product Module", description = "REST APIs for Product Catalogue Browsing, Searching, Filtering, and Admin Management")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get All Products (Paginated)", description = "Retrieves paginated list of products.")
    public ResponseEntity<ApiResponse<PageResponse<ProductDto>>> getAllProducts(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        PageResponse<ProductDto> products = productService.getAllProducts(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", products));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Product By Id", description = "Retrieves product details by product ID.")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable Integer id) {
        ProductDto product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success("Product retrieved successfully", product));
    }

    @GetMapping("/search")
    @Operation(summary = "Search Products", description = "Searches products by keyword.")
    public ResponseEntity<ApiResponse<PageResponse<ProductDto>>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        PageResponse<ProductDto> products = productService.searchProducts(keyword, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Search results retrieved successfully", products));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Filter By Category", description = "Retrieves products filtered by category ID.")
    public ResponseEntity<ApiResponse<PageResponse<ProductDto>>> getProductsByCategory(
            @PathVariable Integer categoryId,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        PageResponse<ProductDto> products = productService.getProductsByCategory(categoryId, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Category products retrieved successfully", products));
    }

    @GetMapping("/filter/price")
    @Operation(summary = "Filter By Price Range", description = "Filters products within specified price boundaries.")
    public ResponseEntity<ApiResponse<PageResponse<ProductDto>>> filterProductsByPrice(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        PageResponse<ProductDto> products = productService.filterProductsByPrice(minPrice, maxPrice, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Price filtered products retrieved successfully", products));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Product (Admin)", description = "Creates a new product. Requires ADMIN role.")
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        ProductDto createdProduct = productService.createProduct(productRequest);
        return new ResponseEntity<>(ApiResponse.success("Product created successfully", createdProduct), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Product (Admin)", description = "Updates an existing product by ID. Requires ADMIN role.")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(@PathVariable Integer id,
                                                                  @Valid @RequestBody ProductRequest productRequest) {
        ProductDto updatedProduct = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", updatedProduct));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete Product (Admin)", description = "Deletes a product by ID. Requires ADMIN role.")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Integer id) {
        ApiResponse<String> response = productService.deleteProduct(id);
        return ResponseEntity.ok(response);
    }
}
