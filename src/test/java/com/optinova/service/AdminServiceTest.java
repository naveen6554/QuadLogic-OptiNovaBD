package com.optinova.service;

import com.optinova.dto.*;
import com.optinova.entity.Category;
import com.optinova.entity.Product;
import com.optinova.entity.User;
import com.optinova.entity.enums.OrderStatus;
import com.optinova.entity.enums.Role;
import com.optinova.exception.DuplicateResourceException;
import com.optinova.exception.InvalidCategoryException;
import com.optinova.exception.ResourceNotFoundException;
import com.optinova.mapper.AdminMapper;
import com.optinova.repository.CategoryRepository;
import com.optinova.repository.OrderRepository;
import com.optinova.repository.ProductRepository;
import com.optinova.repository.UserRepository;
import com.optinova.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private com.optinova.repository.ProductImageRepository productImageRepository;

    @Spy
    private AdminMapper adminMapper = new AdminMapper();

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Category category;
    private Product product;
    private User user;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .categoryId(1)
                .categoryName("Eyeglasses")
                .build();

        product = Product.builder()
                .productId(10)
                .name("Blue Light Glasses")
                .description("Anti-reflective computer glasses")
                .price(new BigDecimal("49.99"))
                .stock(100)
                .category(category)
                .createdAt(LocalDateTime.now())
                .build();

        user = User.builder()
                .userId(1)
                .username("john_doe")
                .email("john@example.com")
                .password("encoded_pass")
                .role(Role.CUSTOMER)
                .build();
    }

    @Test
    @DisplayName("Should successfully add product when category exists")
    void addProduct_Success() {
        AdminCreateProductRequest request = AdminCreateProductRequest.builder()
                .name("Blue Light Glasses")
                .description("Anti-reflective computer glasses")
                .price(new BigDecimal("49.99"))
                .stock(100)
                .categoryId(1)
                .build();

        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        AdminProductResponse response = adminService.addProduct(request);

        assertNotNull(response);
        assertEquals("Blue Light Glasses", response.getName());
        assertEquals(10, response.getProductId());
        verify(categoryRepository, times(1)).findById(1);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw InvalidCategoryException when category ID does not exist")
    void addProduct_InvalidCategory_ThrowsException() {
        AdminCreateProductRequest request = AdminCreateProductRequest.builder()
                .name("Test Glasses")
                .description("Test Description")
                .price(new BigDecimal("29.99"))
                .stock(10)
                .categoryId(999)
                .build();

        when(categoryRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(InvalidCategoryException.class, () -> adminService.addProduct(request));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete product successfully when product exists")
    void deleteProduct_Success() {
        when(productRepository.findById(10)).thenReturn(Optional.of(product));

        adminService.deleteProduct(10);

        verify(productRepository, times(1)).findById(10);
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent product")
    void deleteProduct_NotFound_ThrowsException() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.deleteProduct(999));
        verify(productRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should get all users successfully")
    void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<AdminUserResponse> result = adminService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("john_doe", result.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update user profile and role successfully")
    void updateUser_Success() {
        AdminUpdateUserRequest request = AdminUpdateUserRequest.builder()
                .username("john_updated")
                .email("john.new@example.com")
                .role(Role.ADMIN)
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("john_updated")).thenReturn(false);
        when(userRepository.existsByEmail("john.new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        AdminUserResponse response = adminService.updateUser(1, request);

        assertNotNull(response);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when updating to existing username")
    void updateUser_DuplicateUsername_ThrowsException() {
        AdminUpdateUserRequest request = AdminUpdateUserRequest.builder()
                .username("existing_user")
                .email("john@example.com")
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("existing_user")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> adminService.updateUser(1, request));
    }

    @Test
    @DisplayName("Should return overall revenue report accurately")
    void getOverallRevenue_Success() {
        when(orderRepository.calculateOverallRevenue(OrderStatus.SUCCESS)).thenReturn(new BigDecimal("1500.00"));
        when(orderRepository.countOverallOrders(OrderStatus.SUCCESS)).thenReturn(15L);

        RevenueReportResponse report = adminService.getOverallRevenue();

        assertNotNull(report);
        assertEquals("OVERALL", report.getPeriod());
        assertEquals(new BigDecimal("1500.00"), report.getTotalRevenue());
        assertEquals(15L, report.getTotalOrders());
    }
}
