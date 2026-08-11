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

import java.math.BigDecimal;
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

    @GetMapping("/analytics/daily")
    @Operation(summary = "Get Detailed Daily Analytics", description = "Calculates daily revenue, profit, loss, cost, and hourly chart breakdown.")
    public ResponseEntity<AnalyticsReportResponse> getDetailedDailyAnalytics() {
        log.info("REST GET request for detailed daily analytics");
        return ResponseEntity.ok(adminService.getDetailedDailyAnalytics());
    }

    @GetMapping("/analytics/monthly")
    @Operation(summary = "Get Detailed Monthly Analytics", description = "Calculates monthly revenue, profit, loss, cost, and daily chart breakdown.")
    public ResponseEntity<AnalyticsReportResponse> getDetailedMonthlyAnalytics() {
        log.info("REST GET request for detailed monthly analytics");
        return ResponseEntity.ok(adminService.getDetailedMonthlyAnalytics());
    }

    @GetMapping("/analytics/yearly")
    @Operation(summary = "Get Detailed Yearly Analytics", description = "Calculates yearly revenue, profit, loss, cost, and monthly chart breakdown.")
    public ResponseEntity<AnalyticsReportResponse> getDetailedYearlyAnalytics() {
        log.info("REST GET request for detailed yearly analytics");
        return ResponseEntity.ok(adminService.getDetailedYearlyAnalytics());
    }

    @GetMapping("/analytics/overall")
    @Operation(summary = "Get Detailed Overall Analytics", description = "Calculates lifetime business revenue, profit, loss, cost, and trend chart breakdown.")
    public ResponseEntity<AnalyticsReportResponse> getDetailedOverallAnalytics() {
        log.info("REST GET request for detailed overall analytics");
        return ResponseEntity.ok(adminService.getDetailedOverallAnalytics());
    }

    @GetMapping("/analytics/custom")
    @Operation(summary = "Get Custom Range Analytics", description = "Calculates analytics for custom start and end date range.")
    public ResponseEntity<AnalyticsReportResponse> getCustomAnalytics(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDate) {
        log.info("REST GET request for custom date analytics: {} to {}", startDate, endDate);
        return ResponseEntity.ok(adminService.getCustomAnalytics(startDate, endDate));
    }

    @GetMapping("/invoices")
    @Operation(summary = "Get All Invoices", description = "Retrieves invoice records generated from customer orders.")
    public ResponseEntity<List<InvoiceDto>> getAllInvoices() {
        log.info("REST GET request for all customer invoices");
        return ResponseEntity.ok(adminService.getAllInvoices());
    }

    @GetMapping("/invoices/{id}")
    @Operation(summary = "Get Invoice By ID", description = "Retrieves invoice details for a specific invoice ID or order ID.")
    public ResponseEntity<InvoiceDto> getInvoiceById(@PathVariable("id") String id) {
        log.info("REST GET request for invoice ID: {}", id);
        return ResponseEntity.ok(adminService.getInvoiceById(id));
    }

    @GetMapping("/analytics/export/csv")
    @Operation(summary = "Export Analytics CSV", description = "Generates and returns downloadable CSV report for selected period.")
    public ResponseEntity<byte[]> exportAnalyticsCsv(@RequestParam(defaultValue = "OVERALL") String period) {
        log.info("REST GET request to export CSV for period: {}", period);
        AnalyticsReportResponse report;
        if ("DAILY".equalsIgnoreCase(period)) report = adminService.getDetailedDailyAnalytics();
        else if ("MONTHLY".equalsIgnoreCase(period)) report = adminService.getDetailedMonthlyAnalytics();
        else if ("YEARLY".equalsIgnoreCase(period)) report = adminService.getDetailedYearlyAnalytics();
        else report = adminService.getDetailedOverallAnalytics();

        StringBuilder sb = new StringBuilder();
        sb.append("Date,Order ID,Invoice ID,Customer,Email,Items,Revenue,Product Cost,Expense,Tax,Profit,Loss,Payment Status,Order Status\n");
        if (report.getInvoices() != null) {
            for (InvoiceDto inv : report.getInvoices()) {
                BigDecimal rev = inv.getTotalAmount() != null ? inv.getTotalAmount() : BigDecimal.ZERO;
                BigDecimal cost = rev.multiply(new BigDecimal("0.60")).setScale(2, java.math.RoundingMode.HALF_UP);
                BigDecimal exp = rev.multiply(new BigDecimal("0.02")).setScale(2, java.math.RoundingMode.HALF_UP);
                BigDecimal net = rev.subtract(cost).subtract(exp);
                BigDecimal profit = net.compareTo(BigDecimal.ZERO) > 0 ? net : BigDecimal.ZERO;
                BigDecimal loss = net.compareTo(BigDecimal.ZERO) < 0 ? net.abs() : BigDecimal.ZERO;

                sb.append(inv.getOrderDate() != null ? inv.getOrderDate().toLocalDate() : java.time.LocalDate.now()).append(",")
                  .append(inv.getOrderId()).append(",")
                  .append(inv.getInvoiceId()).append(",")
                  .append("\"").append(inv.getCustomerName() != null ? inv.getCustomerName().replace("\"", "\"\"") : "").append("\",")
                  .append(inv.getCustomerEmail()).append(",")
                  .append(inv.getNumberOfItems() != null ? inv.getNumberOfItems() : 1).append(",")
                  .append(rev).append(",")
                  .append(cost).append(",")
                  .append(exp).append(",")
                  .append(inv.getTax() != null ? inv.getTax() : BigDecimal.ZERO).append(",")
                  .append(profit).append(",")
                  .append(loss).append(",")
                  .append(inv.getPaymentStatus()).append(",")
                  .append(inv.getOrderStatus()).append("\n");
            }
        }
        byte[] csvBytes = sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String filename = "OptiNova-" + period.toUpperCase() + "-Analytics.csv";
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .header(org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, org.springframework.http.HttpHeaders.CONTENT_DISPOSITION)
                .contentType(org.springframework.http.MediaType.parseMediaType("text/csv; charset=utf-8"))
                .body(csvBytes);
    }
}
