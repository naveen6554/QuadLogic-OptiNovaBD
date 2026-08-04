package com.optinova.controller;

import com.optinova.dto.*;
import com.optinova.entity.enums.Role;
import com.optinova.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private AdminProductResponse productResponse;
    private AdminUserResponse userResponse;
    private RevenueReportResponse revenueResponse;

    @BeforeEach
    void setUp() {
        productResponse = AdminProductResponse.builder()
                .productId(1)
                .name("Designer Glasses")
                .price(new BigDecimal("199.99"))
                .stock(50)
                .categoryId(2)
                .categoryName("Designer")
                .build();

        userResponse = AdminUserResponse.builder()
                .userId(1)
                .username("admin_user")
                .email("admin@optinova.com")
                .role(Role.ADMIN)
                .build();

        revenueResponse = RevenueReportResponse.builder()
                .period("DAILY")
                .totalRevenue(new BigDecimal("500.00"))
                .totalOrders(5L)
                .build();
    }

    @Test
    @DisplayName("POST /api/admin/products - Should create product and return HTTP 201 Created")
    void addProduct_ReturnsHttp201() {
        AdminCreateProductRequest request = AdminCreateProductRequest.builder()
                .name("Designer Glasses")
                .description("Luxury glasses")
                .price(new BigDecimal("199.99"))
                .stock(50)
                .categoryId(2)
                .build();

        when(adminService.addProduct(any(AdminCreateProductRequest.class))).thenReturn(productResponse);

        ResponseEntity<AdminProductResponse> response = adminController.addProduct(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Designer Glasses", response.getBody().getName());
        verify(adminService, times(1)).addProduct(any());
    }

    @Test
    @DisplayName("DELETE /api/admin/products/{id} - Should delete product and return HTTP 200 OK")
    void deleteProduct_ReturnsHttp200() {
        doNothing().when(adminService).deleteProduct(1);

        ResponseEntity<ApiResponse<String>> response = adminController.deleteProduct(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        verify(adminService, times(1)).deleteProduct(1);
    }

    @Test
    @DisplayName("GET /api/admin/users - Should return list of users and HTTP 200 OK")
    void getAllUsers_ReturnsHttp200() {
        when(adminService.getAllUsers()).thenReturn(List.of(userResponse));

        ResponseEntity<List<AdminUserResponse>> response = adminController.getAllUsers();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("admin_user", response.getBody().get(0).getUsername());
    }

    @Test
    @DisplayName("PUT /api/admin/users/{id} - Should update user and return HTTP 200 OK")
    void updateUser_ReturnsHttp200() {
        AdminUpdateUserRequest request = AdminUpdateUserRequest.builder()
                .username("admin_user")
                .email("admin@optinova.com")
                .role(Role.ADMIN)
                .build();

        when(adminService.updateUser(eq(1), any(AdminUpdateUserRequest.class))).thenReturn(userResponse);

        ResponseEntity<AdminUserResponse> response = adminController.updateUser(1, request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("admin_user", response.getBody().getUsername());
    }

    @Test
    @DisplayName("GET /api/admin/revenue/daily - Should return daily revenue report")
    void getDailyRevenue_ReturnsHttp200() {
        when(adminService.getDailyRevenue()).thenReturn(revenueResponse);

        ResponseEntity<RevenueReportResponse> response = adminController.getDailyRevenue();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("DAILY", response.getBody().getPeriod());
    }
}
