package com.optinova.service.impl;

import com.optinova.dto.*;
import com.optinova.entity.Category;
import com.optinova.entity.Product;
import com.optinova.entity.ProductImage;
import com.optinova.entity.User;
import com.optinova.entity.enums.OrderStatus;
import com.optinova.exception.DuplicateResourceException;
import com.optinova.exception.InvalidCategoryException;
import com.optinova.exception.ResourceNotFoundException;
import com.optinova.mapper.AdminMapper;
import com.optinova.repository.CategoryRepository;
import com.optinova.repository.OrderRepository;
import com.optinova.repository.ProductImageRepository;
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
    private final ProductImageRepository productImageRepository;
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AdminProductResponse addProduct(AdminCreateProductRequest request) {
        log.info("Admin creating new product with name: {}", request.getName());

        // 1. Validate Category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new InvalidCategoryException("Invalid Category ID: " + request.getCategoryId()));

        // 2. Prevent duplicate product insertion if a product with the same name already exists
        if (productRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new DuplicateResourceException("Product with name '" + request.getName().trim() + "' already exists in inventory.");
        }

        // 3. Build Product Entity & Save to 'products' table
        Product product = Product.builder()
                .name(request.getName().trim())
                .description(request.getDescription() != null ? request.getDescription().trim() : "")
                .price(request.getPrice())
                .stock(request.getStock())
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Product saved successfully in 'products' table with ID: {}", savedProduct.getProductId());

        // 3. Explicitly construct and save ProductImage records in 'productimages' table
        List<ProductImage> imageEntities = new java.util.ArrayList<>();

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (String url : request.getImageUrls()) {
                if (url != null && !url.isBlank()) {
                    imageEntities.add(ProductImage.builder()
                            .imageUrl(url.trim())
                            .product(savedProduct)
                            .build());
                }
            }
        } else if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            imageEntities.add(ProductImage.builder()
                    .imageUrl(request.getImageUrl().trim())
                    .product(savedProduct)
                    .build());
        }

        if (!imageEntities.isEmpty()) {
            List<ProductImage> savedImages = productImageRepository.saveAll(imageEntities);
            savedProduct.setImages(savedImages);
            log.info("Saved {} image record(s) in 'productimages' table for Product ID: {}", savedImages.size(), savedProduct.getProductId());
        }

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

    @Override
    @Transactional(readOnly = true)
    public AnalyticsReportResponse getDetailedDailyAnalytics() {
        LocalDate today = LocalDate.now();
        return generateDetailedAnalytics("DAILY", today.atStartOfDay(), today.atTime(LocalTime.MAX));
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsReportResponse getDetailedMonthlyAnalytics() {
        YearMonth currentMonth = YearMonth.now();
        return generateDetailedAnalytics("MONTHLY", currentMonth.atDay(1).atStartOfDay(), currentMonth.atEndOfMonth().atTime(LocalTime.MAX));
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsReportResponse getDetailedYearlyAnalytics() {
        int currentYear = LocalDate.now().getYear();
        return generateDetailedAnalytics("YEARLY", LocalDate.of(currentYear, 1, 1).atStartOfDay(), LocalDate.of(currentYear, 12, 31).atTime(LocalTime.MAX));
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsReportResponse getDetailedOverallAnalytics() {
        return generateDetailedAnalytics("OVERALL", null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsReportResponse getCustomAnalytics(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : LocalDate.now().atTime(LocalTime.MAX);
        return generateDetailedAnalytics("CUSTOM", start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDto> getAllInvoices() {
        List<com.optinova.entity.Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();
        return orders.stream().map(this::mapOrderToInvoice).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDto getInvoiceById(String invoiceId) {
        String cleanId = invoiceId != null ? invoiceId.replace("INV-", "").replace("2026-", "") : "";
        List<com.optinova.entity.Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();
        for (com.optinova.entity.Order order : orders) {
            String invId = mapOrderToInvoice(order).getInvoiceId();
            if (invId.equalsIgnoreCase(invoiceId) || order.getOrderId().equalsIgnoreCase(invoiceId) || order.getOrderId().endsWith(cleanId)) {
                return mapOrderToInvoice(order);
            }
        }
        throw new ResourceNotFoundException("Invoice", "id", invoiceId);
    }

    private InvoiceDto mapOrderToInvoice(com.optinova.entity.Order order) {
        String numPart = order.getOrderId().replaceAll("[^0-9]", "");
        if (numPart.length() < 6) {
            numPart = String.format("%06d", Math.abs(order.getOrderId().hashCode()) % 1000000);
        }
        String invoiceId = "INV-2026-" + numPart;
        User user = order.getUser();
        String customerName = user != null ? user.getUsername() : "OptiNova Customer";
        String customerEmail = user != null ? user.getEmail() : "customer@optinova.com";
        BigDecimal total = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal tax = total.multiply(new BigDecimal("0.18")).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal subtotal = total.subtract(tax);

        int itemCount = order.getOrderItems() != null && !order.getOrderItems().isEmpty() ?
                order.getOrderItems().stream().mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 1).sum() : 1;

        String payStatus = order.getStatus() == OrderStatus.SUCCESS ? "PAID" : (order.getStatus() == OrderStatus.PENDING ? "UNPAID" : "REFUNDED");
        String invStatus = order.getStatus() == OrderStatus.SUCCESS ? "ISSUED" : (order.getStatus() == OrderStatus.PENDING ? "DRAFT" : "CANCELLED");

        return InvoiceDto.builder()
                .invoiceId(invoiceId)
                .orderId(order.getOrderId())
                .customerName(customerName)
                .customerEmail(customerEmail)
                .customerPhone("+91 98765 43210")
                .customerAddress("123 OptiNova Tower, Suite 400, Bangalore")
                .orderDate(order.getCreatedAt())
                .numberOfItems(itemCount)
                .subtotal(subtotal)
                .discount(BigDecimal.ZERO)
                .tax(tax)
                .shipping(BigDecimal.ZERO)
                .totalAmount(total)
                .paymentStatus(payStatus)
                .invoiceStatus(invStatus)
                .orderStatus(order.getStatus().name())
                .build();
    }

    private AnalyticsReportResponse generateDetailedAnalytics(String period, LocalDateTime start, LocalDateTime end) {
        log.info("Generating detailed analytics report for period: {}, from {} to {}", period, start, end);

        List<com.optinova.entity.Order> orders;
        if (start != null && end != null) {
            orders = orderRepository.findAllByCreatedAtBetweenOrderByCreatedAtDesc(start, end);
        } else {
            orders = orderRepository.findAllByOrderByCreatedAtDesc();
        }

        long totalOrders = orders.size();
        long completedOrders = orders.stream().filter(o -> o.getStatus() == OrderStatus.SUCCESS).count();
        long pendingOrders = orders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count();
        long cancelledOrders = orders.stream().filter(o -> o.getStatus() == OrderStatus.FAILED).count();

        long totalCustomers = orders.stream().map(o -> o.getUser() != null ? o.getUser().getUserId() : 1).distinct().count();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal refundedAmount = BigDecimal.ZERO;
        long totalProductsSold = 0;
        BigDecimal totalCostAmount = BigDecimal.ZERO;

        for (com.optinova.entity.Order o : orders) {
            if (o.getStatus() == OrderStatus.SUCCESS) {
                totalRevenue = totalRevenue.add(o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO);
                if (o.getOrderItems() != null && !o.getOrderItems().isEmpty()) {
                    for (com.optinova.entity.OrderItem item : o.getOrderItems()) {
                        int q = item.getQuantity() != null ? item.getQuantity() : 1;
                        totalProductsSold += q;
                        BigDecimal unitCost = item.getProduct() != null ? item.getProduct().getEffectiveCostPrice() : BigDecimal.ZERO;
                        totalCostAmount = totalCostAmount.add(unitCost.multiply(BigDecimal.valueOf(q)));
                    }
                } else {
                    totalProductsSold += 1;
                    BigDecimal estCost = (o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO).multiply(new BigDecimal("0.60"));
                    totalCostAmount = totalCostAmount.add(estCost);
                }
            } else if (o.getStatus() == OrderStatus.FAILED) {
                refundedAmount = refundedAmount.add(o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO);
            }
        }

        BigDecimal taxAmount = totalRevenue.multiply(new BigDecimal("0.18")).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal shippingRevenue = BigDecimal.ZERO;
        BigDecimal shippingCost = BigDecimal.ZERO;
        BigDecimal otherExpenses = totalRevenue.multiply(new BigDecimal("0.02")).setScale(2, java.math.RoundingMode.HALF_UP);

        BigDecimal totalExpenses = totalCostAmount.add(otherExpenses).add(refundedAmount).add(shippingCost);
        BigDecimal grossProfit = totalRevenue.subtract(totalCostAmount);
        BigDecimal netProfitLoss = totalRevenue.subtract(totalExpenses);

        BigDecimal totalProfit = netProfitLoss.compareTo(BigDecimal.ZERO) > 0 ? netProfitLoss : BigDecimal.ZERO;
        BigDecimal totalLoss = netProfitLoss.compareTo(BigDecimal.ZERO) < 0 ? netProfitLoss.abs() : BigDecimal.ZERO;

        BigDecimal averageOrderValue = completedOrders > 0 ?
                totalRevenue.divide(BigDecimal.valueOf(completedOrders), 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;

        List<InvoiceDto> invoices = orders.stream().map(this::mapOrderToInvoice).collect(Collectors.toList());
        List<AnalyticsReportResponse.AnalyticsChartPoint> chartData = buildChartSeries(period, orders, start, end);

        return AnalyticsReportResponse.builder()
                .period(period)
                .startDate(start)
                .endDate(end)
                .generatedAt(LocalDateTime.now())
                .totalOrders(totalOrders)
                .completedOrders(completedOrders)
                .pendingOrders(pendingOrders)
                .cancelledOrders(cancelledOrders)
                .totalProductsSold(totalProductsSold)
                .totalCustomers(totalCustomers)
                .totalRevenue(totalRevenue)
                .totalCostAmount(totalCostAmount)
                .totalExpenses(totalExpenses)
                .totalProfit(totalProfit)
                .totalLoss(totalLoss)
                .netProfitLoss(netProfitLoss)
                .grossProfit(grossProfit)
                .averageOrderValue(averageOrderValue)
                .refundedAmount(refundedAmount)
                .discountAmount(discountAmount)
                .taxAmount(taxAmount)
                .shippingRevenue(shippingRevenue)
                .shippingCost(shippingCost)
                .otherRevenue(BigDecimal.ZERO)
                .otherExpenses(otherExpenses)
                .chartData(chartData)
                .invoices(invoices)
                .build();
    }

    private List<AnalyticsReportResponse.AnalyticsChartPoint> buildChartSeries(String period, List<com.optinova.entity.Order> orders, LocalDateTime start, LocalDateTime end) {
        List<AnalyticsReportResponse.AnalyticsChartPoint> points = new java.util.ArrayList<>();
        if ("DAILY".equalsIgnoreCase(period)) {
            for (int h = 0; h < 24; h += 2) {
                int hourStart = h;
                int hourEnd = h + 1;
                String label = String.format("%02d:00", h);

                BigDecimal rev = BigDecimal.ZERO;
                long cnt = 0;

                for (com.optinova.entity.Order o : orders) {
                    if (o.getCreatedAt() != null && o.getCreatedAt().getHour() >= hourStart && o.getCreatedAt().getHour() <= hourEnd && o.getStatus() == OrderStatus.SUCCESS) {
                        rev = rev.add(o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO);
                        cnt++;
                    }
                }
                BigDecimal cost = rev.multiply(new BigDecimal("0.60"));
                BigDecimal prof = rev.subtract(cost);

                points.add(AnalyticsReportResponse.AnalyticsChartPoint.builder()
                        .label(label)
                        .revenue(rev)
                        .cost(cost)
                        .profit(prof)
                        .loss(BigDecimal.ZERO)
                        .orders(cnt)
                        .build());
            }
        } else if ("MONTHLY".equalsIgnoreCase(period)) {
            int days = YearMonth.now().lengthOfMonth();
            for (int d = 1; d <= days; d += 3) {
                final int day = d;
                String label = "Day " + d;
                BigDecimal rev = BigDecimal.ZERO;
                long cnt = 0;

                for (com.optinova.entity.Order o : orders) {
                    if (o.getCreatedAt() != null && o.getCreatedAt().getDayOfMonth() == day && o.getStatus() == OrderStatus.SUCCESS) {
                        rev = rev.add(o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO);
                        cnt++;
                    }
                }
                BigDecimal cost = rev.multiply(new BigDecimal("0.60"));
                BigDecimal prof = rev.subtract(cost);

                points.add(AnalyticsReportResponse.AnalyticsChartPoint.builder()
                        .label(label)
                        .revenue(rev)
                        .cost(cost)
                        .profit(prof)
                        .loss(BigDecimal.ZERO)
                        .orders(cnt)
                        .build());
            }
        } else {
            String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            for (int m = 1; m <= 12; m++) {
                final int monthNum = m;
                String label = months[m - 1];
                BigDecimal rev = BigDecimal.ZERO;
                long cnt = 0;

                for (com.optinova.entity.Order o : orders) {
                    if (o.getCreatedAt() != null && o.getCreatedAt().getMonthValue() == monthNum && o.getStatus() == OrderStatus.SUCCESS) {
                        rev = rev.add(o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO);
                        cnt++;
                    }
                }
                BigDecimal cost = rev.multiply(new BigDecimal("0.60"));
                BigDecimal prof = rev.subtract(cost);

                points.add(AnalyticsReportResponse.AnalyticsChartPoint.builder()
                        .label(label)
                        .revenue(rev)
                        .cost(cost)
                        .profit(prof)
                        .loss(BigDecimal.ZERO)
                        .orders(cnt)
                        .build());
            }
        }
        return points;
    }
}
