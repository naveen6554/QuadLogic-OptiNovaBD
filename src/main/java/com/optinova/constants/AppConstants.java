package com.optinova.constants;

/**
 * Application-wide constant definitions for OptiNova Backend.
 * Centralized location for configuration keys, role names, default pagination values, and response messages.
 */
public final class AppConstants {

    private AppConstants() {
        // Private constructor to prevent instantiation
    }

    // API Base Paths
    public static final String API_BASE_PATH = "/api/v1";
    public static final String AUTH_BASE_PATH = API_BASE_PATH + "/auth";
    public static final String USER_BASE_PATH = API_BASE_PATH + "/users";
    public static final String CATEGORY_BASE_PATH = API_BASE_PATH + "/categories";
    public static final String PRODUCT_BASE_PATH = API_BASE_PATH + "/products";
    public static final String CART_BASE_PATH = API_BASE_PATH + "/cart";
    public static final String ORDER_BASE_PATH = API_BASE_PATH + "/orders";
    public static final String ADMIN_BASE_PATH = API_BASE_PATH + "/admin";

    // Pagination Constants
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";

    // Security & JWT Constants
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    // Roles
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
}
