package com.optinova.service.impl;

import com.optinova.dto.*;
import com.optinova.entity.Category;
import com.optinova.entity.Product;
import com.optinova.entity.User;
import com.optinova.entity.enums.OrderStatus;
import com.optinova.exception.DuplicateResourceException;
import com.optinova.exception.InvalidCategoryException;
import com.optinova.exception.ResourceNotFoundException;
import com.optinova.mapper.AdminMapper;
import com.optinova.repository.CategoryRepository;
import com.optinova.repository.OrderRepository;
import com.optinova.repository.ProductRepository;
import com.optinova.repository.UserRepository;
import com.optinova.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enterprise Service Implementation managing Admin operations: product creation/deletion,
 * user profile & role administration, and financial analytics report generation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AdminProductResponse addProduct(AdminCreateProductRequest request) {
        log.info("Admin creating new product with name: {}", request.getName());

        // 1. Validate Category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new InvalidCategoryException("Invalid Category ID: " + request.getCategoryId()));

        // 2. Build Product Entity & Immediate Inventory Initialization
        Product product = Product.builder()
                .name(request.getName().trim())
                .description(request.getDescription().trim())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Product saved successfully with ID: {}", savedProduct.getProductId());

        return adminMapper.toProductResponse(savedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Integer productId) {
        log.info("Admin deleting product with ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        productRepository.delete(product);
        log.info("Product with ID: {} successfully deleted.", productId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserResponse> getAllUsers() {
        log.info("Fetching all registered users for Admin panel");
        return userRepository.findAll().stream()
                .map(adminMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AdminUserResponse updateUser(Integer userId, AdminUpdateUserRequest request) {
        log.info("Admin updating user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Validate unique username if changed
        if (!user.getUsername().equalsIgnoreCase(request.getUsername().trim())) {
            if (userRepository.existsByUsername(request.getUsername().trim())) {
                throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already taken.");
            }
            user.setUsername(request.getUsername().trim());
        }

        // Validate unique email if changed
        if (!user.getEmail().equalsIgnoreCase(request.getEmail().trim())) {
            if (userRepository.existsByEmail(request.getEmail().trim())) {
                throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already registered.");
            }
            user.setEmail(request.getEmail().trim());
        }

        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
        }

        // Update Role & Permissions
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        User updatedUser = userRepository.save(user);
        log.info("User ID: {} updated successfully.", updatedUser.getUserId());

        return adminMapper.toUserResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public RevenueReportResponse getDailyRevenue() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        return generateRevenueReport("DAILY", startOfDay, endOfDay);
    }

    @Override
    @Transactional(readOnly = true)
    public RevenueReportResponse getMonthlyRevenue() {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(LocalTime.MAX);

        return generateRevenueReport("MONTHLY", startOfMonth, endOfMonth);
    }

    @Override
    @Transactional(readOnly = true)
    public RevenueReportResponse getYearlyRevenue() {
        int currentYear = LocalDate.now().getYear();
        LocalDateTime startOfYear = LocalDate.of(currentYear, 1, 1).atStartOfDay();
        LocalDateTime endOfYear = LocalDate.of(currentYear, 12, 31).atTime(LocalTime.MAX);

        return generateRevenueReport("YEARLY", startOfYear, endOfYear);
    }

    @Override
    @Transactional(readOnly = true)
    public RevenueReportResponse getOverallRevenue() {
        log.info("Calculating overall lifetime revenue report");

        BigDecimal totalRevenue = orderRepository.calculateOverallRevenue(OrderStatus.SUCCESS);
        Long totalOrders = orderRepository.countOverallOrders(OrderStatus.SUCCESS);

        return RevenueReportResponse.builder()
                .period("OVERALL")
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .totalOrders(totalOrders != null ? totalOrders : 0L)
                .startDate(null)
                .endDate(null)
                .generatedAt(LocalDateTime.now())
                .build();
    }

    private RevenueReportResponse generateRevenueReport(String period, LocalDateTime start, LocalDateTime end) {
        log.info("Calculating {} revenue report from {} to {}", period, start, end);

        BigDecimal revenue = orderRepository.calculateRevenueBetweenDates(OrderStatus.SUCCESS, start, end);
        Long orderCount = orderRepository.countOrdersBetweenDates(OrderStatus.SUCCESS, start, end);

        return RevenueReportResponse.builder()
                .period(period)
                .totalRevenue(revenue != null ? revenue : BigDecimal.ZERO)
                .totalOrders(orderCount != null ? orderCount : 0L)
                .startDate(start)
                .endDate(end)
                .generatedAt(LocalDateTime.now())
                .build();
    }
}
