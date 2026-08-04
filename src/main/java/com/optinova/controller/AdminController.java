package com.optinova.controller;

import com.optinova.dto.*;
import com.optinova.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Enterprise REST Controller exposing administrative operations for OptiNova platform.
 * Endpoints are restricted strictly to ADMIN users via @PreAuthorize("hasRole('ADMIN')").
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Module", description = "REST APIs for Product Management, User Administration, and Business Analytics")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    // =========================================================================
    // 1. PRODUCT MANAGEMENT
    // =========================================================================

    @PostMapping("/products")
    @Operation(summary = "Add New Product", description = "Creates a new product with category validation and inventory initialization.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully", content = @Content(schema = @Schema(implementation = AdminProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request details or category ID"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<AdminProductResponse> addProduct(@Valid @RequestBody AdminCreateProductRequest request) {
        log.info("REST POST request to create product: {}", request.getName());
        AdminProductResponse response = adminService.addProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/products/{id}")
    @Operation(summary = "Delete Product", description = "Deletes an existing product by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<com.optinova.dto.ApiResponse<String>> deleteProduct(@PathVariable("id") Integer id) {
        log.info("REST DELETE request for product ID: {}", id);
        adminService.deleteProduct(id);
        return ResponseEntity.ok(com.optinova.dto.ApiResponse.success("Product deleted successfully."));
    }

    // =========================================================================
    // 2. USER MANAGEMENT
    // =========================================================================

    @GetMapping("/users")
    @Operation(summary = "Get All Users", description = "Retrieves a complete list of registered users for management.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {
        log.info("REST GET request to retrieve all users");
        List<AdminUserResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Update User Details", description = "Updates username, email, password, and role for a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(schema = @Schema(implementation = AdminUserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error or duplicate email/username"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<AdminUserResponse> updateUser(
            @PathVariable("id") Integer id,
            @Valid @RequestBody AdminUpdateUserRequest request) {
        log.info("REST PUT request to update user ID: {}", id);
        AdminUserResponse response = adminService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    // =========================================================================
    // 3. BUSINESS ANALYTICS & REVENUE REPORTS
    // =========================================================================

    @GetMapping("/revenue/daily")
    @Operation(summary = "Get Daily Revenue", description = "Calculates total revenue and order count for the current day.")
    public ResponseEntity<RevenueReportResponse> getDailyRevenue() {
        log.info("REST GET request for daily revenue analytics");
        return ResponseEntity.ok(adminService.getDailyRevenue());
    }

    @GetMapping("/revenue/monthly")
    @Operation(summary = "Get Monthly Revenue", description = "Calculates total revenue and order count for the current calendar month.")
    public ResponseEntity<RevenueReportResponse> getMonthlyRevenue() {
        log.info("REST GET request for monthly revenue analytics");
        return ResponseEntity.ok(adminService.getMonthlyRevenue());
    }

    @GetMapping("/revenue/yearly")
    @Operation(summary = "Get Yearly Revenue", description = "Calculates total revenue and order count for the current calendar year.")
    public ResponseEntity<RevenueReportResponse> getYearlyRevenue() {
        log.info("REST GET request for yearly revenue analytics");
        return ResponseEntity.ok(adminService.getYearlyRevenue());
    }

    @GetMapping("/revenue/overall")
    @Operation(summary = "Get Overall Revenue", description = "Calculates overall lifetime revenue and total successful order count.")
    public ResponseEntity<RevenueReportResponse> getOverallRevenue() {
        log.info("REST GET request for overall lifetime revenue analytics");
        return ResponseEntity.ok(adminService.getOverallRevenue());
    }
}
