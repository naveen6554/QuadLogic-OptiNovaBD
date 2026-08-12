package com.optinova.config;

import com.optinova.entity.Category;
import com.optinova.entity.Product;
import com.optinova.entity.ProductImage;
import com.optinova.entity.User;
import com.optinova.entity.enums.Role;
import com.optinova.repository.CategoryRepository;
import com.optinova.repository.ProductRepository;
import com.optinova.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Automatically seeds default Administrator account, Categories, and Eyewear Products on application startup if missing.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedAdminUser();
        seedCategoriesAndProducts();
    }

    private void seedAdminUser() {
        userRepository.findByEmail("optiadmin@optinova.com")
                .or(() -> userRepository.findByUsername("optiadmin"))
                .ifPresentOrElse(
                        existingAdmin -> {
                            existingAdmin.setUsername("optiadmin");
                            existingAdmin.setEmail("optiadmin@optinova.com");
                            existingAdmin.setPassword(passwordEncoder.encode("admin@123"));
                            existingAdmin.setRole(Role.ADMIN);
                            userRepository.save(existingAdmin);
                            log.info("Default Admin account 'optiadmin' verified and updated.");
                        },
                        () -> {
                            User newAdmin = User.builder()
                                    .username("optiadmin")
                                    .email("optiadmin@optinova.com")
                                    .password(passwordEncoder.encode("admin@123"))
                                    .role(Role.ADMIN)
                                    .build();
                            userRepository.save(newAdmin);
                            log.info("Default Admin account 'optiadmin' created successfully with password 'admin@123'.");
                        }
                );
    }

    private void seedCategoriesAndProducts() {
        if (categoryRepository.count() > 0 && productRepository.count() > 0) {
            log.info("Categories and Products already exist in database. Skipping seeding.");
            return;
        }

        log.info("Seeding default Eyewear Categories and Premium Eyewear Products...");

        Category prescription = categoryRepository.findByCategoryName("Prescription Glasses")
                .orElseGet(() -> categoryRepository.save(Category.builder().categoryName("Prescription Glasses").build()));

        Category sunglasses = categoryRepository.findByCategoryName("Sunglasses")
                .orElseGet(() -> categoryRepository.save(Category.builder().categoryName("Sunglasses").build()));

        Category blueLight = categoryRepository.findByCategoryName("Blue Light Blocking")
                .orElseGet(() -> categoryRepository.save(Category.builder().categoryName("Blue Light Blocking").build()));

        Category reading = categoryRepository.findByCategoryName("Reading Glasses")
                .orElseGet(() -> categoryRepository.save(Category.builder().categoryName("Reading Glasses").build()));

        if (productRepository.count() == 0) {
            createProduct("OptiNova Titan Precision Eyewear", "Ultra-lightweight Japanese Beta-Titanium frame with anti-reflective ZEISS precision optics.", new BigDecimal("249.99"), new BigDecimal("120.00"), 45, prescription, "https://images.unsplash.com/photo-1572635196237-14b3f281503f?auto=format&fit=crop&w=800&q=80");
            createProduct("Starlight Gold Aviator Sunglasses", "Classic pilot frame electroplated in 24k champagne gold with polarized UV400 lenses.", new BigDecimal("189.50"), new BigDecimal("85.00"), 30, sunglasses, "https://images.unsplash.com/photo-1511499767150-a48a237f0083?auto=format&fit=crop&w=800&q=80");
            createProduct("CyberShield Anti-Glare Blue Cut", "Engineered for high-performance screen work. Filters 98% of harmful 450nm blue light emissions.", new BigDecimal("129.00"), new BigDecimal("55.00"), 60, blueLight, "https://images.unsplash.com/photo-1591076482161-42ce6da69f67?auto=format&fit=crop&w=800&q=80");
            createProduct("Velvet Matte Acetate Square", "Handcrafted Italian Bio-Acetate in matte obsidian black with German spring hinges.", new BigDecimal("210.00"), new BigDecimal("95.00"), 25, prescription, "https://images.unsplash.com/photo-1577803645773-f96470509666?auto=format&fit=crop&w=800&q=80");
            createProduct("OptiNova Executive Magnifier", "Precision progressive reading glasses with anti-scratch coating and slim leather holster.", new BigDecimal("145.00"), new BigDecimal("60.00"), 40, reading, "https://images.unsplash.com/photo-1508296695146-257a814070b4?auto=format&fit=crop&w=800&q=80");
            log.info("Successfully seeded 5 premium Eyewear products into database!");
        }
    }

    private void createProduct(String name, String desc, BigDecimal price, BigDecimal costPrice, int stock, Category category, String imageUrl) {
        Product product = Product.builder()
                .name(name)
                .description(desc)
                .price(price)
                .costPrice(costPrice)
                .stock(stock)
                .category(category)
                .build();

        ProductImage img = ProductImage.builder()
                .imageUrl(imageUrl)
                .product(product)
                .build();

        product.getImages().add(img);
        productRepository.save(product);
    }
}
