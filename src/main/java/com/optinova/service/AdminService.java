package com.optinova.service;

import com.optinova.dto.*;

import java.util.List;

/**
 * Service interface defining functional contract for Administrator management operations.
 * Covers product lifecycle management, user administration, and financial analytics reporting.
 */
public interface AdminService {

    /**
     * Creates a new product with validated category and inventory.
     *
     * @param request Product creation payload
     * @return AdminProductResponse with saved product details
     */
    AdminProductResponse addProduct(AdminCreateProductRequest request);

    /**
     * Deletes a product by ID.
     *
     * @param productId Primary key of the product
     */
    void deleteProduct(Integer productId);

    /**
     * Retrieves all registered users in the system.
     *
     * @return List of AdminUserResponse DTOs
     */
    List<AdminUserResponse> getAllUsers();

    /**
     * Updates user details, username, email, password, and security role.
     *
     * @param userId Primary key of the user to update
     * @param request Update payload
     * @return AdminUserResponse with updated user details
     */
    AdminUserResponse updateUser(Integer userId, AdminUpdateUserRequest request);

    /**
     * Calculates daily revenue report for the current day.
     *
     * @return RevenueReportResponse DTO
     */
    RevenueReportResponse getDailyRevenue();

    /**
     * Calculates monthly revenue report for the current calendar month.
     *
     * @return RevenueReportResponse DTO
     */
    RevenueReportResponse getMonthlyRevenue();

    /**
     * Calculates yearly revenue report for the current calendar year.
     *
     * @return RevenueReportResponse DTO
     */
    RevenueReportResponse getYearlyRevenue();

    /**
     * Calculates overall lifetime revenue report.
     *
     * @return RevenueReportResponse DTO
     */
    RevenueReportResponse getOverallRevenue();

    AnalyticsReportResponse getDetailedDailyAnalytics();
    AnalyticsReportResponse getDetailedMonthlyAnalytics();
    AnalyticsReportResponse getDetailedYearlyAnalytics();
    AnalyticsReportResponse getDetailedOverallAnalytics();
    AnalyticsReportResponse getCustomAnalytics(java.time.LocalDate startDate, java.time.LocalDate endDate);
    List<InvoiceDto> getAllInvoices();
    InvoiceDto getInvoiceById(String invoiceId);
}
