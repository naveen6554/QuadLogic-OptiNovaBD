package com.optinova.controller;

import com.optinova.constants.AppConstants;
import com.optinova.dto.ApiResponse;
import com.optinova.dto.ProductImageDto;
import com.optinova.dto.ProductImageRequest;
import com.optinova.service.ProductImageService;
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
 * REST Controller exposing Product Image Gallery management APIs.
 */
@RestController
@RequestMapping(AppConstants.API_BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Product Images Module", description = "REST APIs for Product Image Gallery Management")
public class ProductImageController {

    private final ProductImageService productImageService;

    @GetMapping("/products/{productId}/images")
    @Operation(summary = "Get Images By Product ID", description = "Retrieves all gallery images associated with a specific product.")
    public ResponseEntity<ApiResponse<List<ProductImageDto>>> getImagesByProductId(@PathVariable Integer productId) {
        List<ProductImageDto> images = productImageService.getImagesByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success("Product images retrieved successfully", images));
    }

    @PostMapping("/products/{productId}/images")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add Image To Product (Admin)", description = "Adds a new gallery image to a product. Requires ADMIN role.")
    public ResponseEntity<ApiResponse<ProductImageDto>> addImageToProduct(
            @PathVariable Integer productId,
            @Valid @RequestBody ProductImageRequest request) {
        ProductImageDto createdImage = productImageService.addImageToProduct(productId, request);
        return new ResponseEntity<>(ApiResponse.success("Product image added successfully", createdImage), HttpStatus.CREATED);
    }

    @PutMapping("/images/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Product Image (Admin)", description = "Updates image URL by image ID. Requires ADMIN role.")
    public ResponseEntity<ApiResponse<ProductImageDto>> updateProductImage(
            @PathVariable Integer imageId,
            @Valid @RequestBody ProductImageRequest request) {
        ProductImageDto updatedImage = productImageService.updateProductImage(imageId, request);
        return ResponseEntity.ok(ApiResponse.success("Product image updated successfully", updatedImage));
    }

    @DeleteMapping("/images/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete Product Image (Admin)", description = "Deletes an image from the gallery by image ID. Requires ADMIN role.")
    public ResponseEntity<ApiResponse<String>> deleteProductImage(@PathVariable Integer imageId) {
        ApiResponse<String> response = productImageService.deleteProductImage(imageId);
        return ResponseEntity.ok(response);
    }
}
