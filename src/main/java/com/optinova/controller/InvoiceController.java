package com.optinova.controller;

import com.optinova.security.CustomUserDetails;
import com.optinova.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/orders", "/api/orders"})
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Invoice Module", description = "REST APIs for Downloading Dynamic PDF Invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/{orderId}/invoice")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Download PDF Invoice", description = "Generates and returns professional A4 PDF invoice for an order.")
    public ResponseEntity<byte[]> downloadInvoice(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String orderId) {

        boolean isAdmin = userDetails != null && userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));
        Integer currentUserId = userDetails != null && userDetails.getUser() != null ? userDetails.getUser().getUserId() : 1;

        byte[] pdfBytes = invoiceService.generateInvoicePdf(orderId, currentUserId, isAdmin);

        String cleanNum = orderId.replaceAll("[^0-9]", "");
        String filename = "INV-" + (cleanNum.length() > 4 ? cleanNum : "202600145") + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
