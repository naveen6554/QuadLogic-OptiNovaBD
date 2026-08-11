package com.optinova.service;

public interface InvoiceService {
    
    /**
     * Generates dynamic PDF invoice byte array for a given order ID.
     *
     * @param orderId       The unique order ID
     * @param currentUserId The ID of the authenticated user requesting invoice
     * @param isAdmin       Whether the user is an administrator
     * @return Raw PDF document byte array
     */
    byte[] generateInvoicePdf(String orderId, Integer currentUserId, boolean isAdmin);
}
