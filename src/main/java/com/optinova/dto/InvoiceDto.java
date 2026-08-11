package com.optinova.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representing generated order invoice summary")
public class InvoiceDto {
    private String invoiceId;
    private String orderId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;
    private LocalDateTime orderDate;
    private Integer numberOfItems;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal shipping;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String invoiceStatus;
    private String orderStatus;
}
