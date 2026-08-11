package com.optinova.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO containing detailed business analytics and financial reports")
public class AnalyticsReportResponse {

    private String period;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime generatedAt;

    private Long totalOrders;
    private Long completedOrders;
    private Long pendingOrders;
    private Long cancelledOrders;
    private Long totalProductsSold;
    private Long totalCustomers;

    private BigDecimal totalRevenue;
    private BigDecimal totalCostAmount;
    private BigDecimal totalExpenses;
    private BigDecimal totalProfit;
    private BigDecimal totalLoss;
    private BigDecimal netProfitLoss;
    private BigDecimal grossProfit;

    private BigDecimal averageOrderValue;
    private BigDecimal refundedAmount;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;

    private BigDecimal shippingRevenue;
    private BigDecimal shippingCost;
    private BigDecimal otherRevenue;
    private BigDecimal otherExpenses;

    private List<AnalyticsChartPoint> chartData;
    private List<InvoiceDto> invoices;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnalyticsChartPoint {
        private String label;
        private BigDecimal revenue;
        private BigDecimal cost;
        private BigDecimal profit;
        private BigDecimal loss;
        private Long orders;
    }
}
