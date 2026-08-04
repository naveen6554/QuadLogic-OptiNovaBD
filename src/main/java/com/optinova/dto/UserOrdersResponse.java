package com.optinova.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO payload for user SUCCESS order history matching the target JSON schema.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOrdersResponse {

    private String role;
    private String username;
    private OrderProductsWrapper orders;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderProductsWrapper {
        private List<SuccessOrderProductDto> products;
    }
}
