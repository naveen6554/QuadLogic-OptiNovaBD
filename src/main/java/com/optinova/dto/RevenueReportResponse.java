package com.optinova.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO returning financial revenue calculations and statistics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO response containing business revenue report analytics")
public class RevenueReportResponse {

    @Schema(description = "Reporting time period (DAILY, MONTHLY, YEARLY, OVERALL)", example = "DAILY")
    private String period;

    @Schema(description = "Total accumulated revenue for the period", example = "1450.50")
    private BigDecimal totalRevenue;

    @Schema(description = "Total number of completed orders in period", example = "12")
    private Long totalOrders;

    @Schema(description = "Start datetime boundary for report calculation")
    private LocalDateTime startDate;

    @Schema(description = "End datetime boundary for report calculation")
    private LocalDateTime endDate;

    @Schema(description = "Timestamp when report was generated")
    private LocalDateTime generatedAt;
}
