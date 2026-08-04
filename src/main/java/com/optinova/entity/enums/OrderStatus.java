package com.optinova.entity.enums;

/**
 * Enumeration representing order status lifecycle.
 * Aligns with database constraint ENUM('PENDING', 'SUCCESS', 'FAILED').
 */
public enum OrderStatus {
    PENDING,
    SUCCESS,
    FAILED
}
