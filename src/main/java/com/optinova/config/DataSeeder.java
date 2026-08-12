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
import java.util.HashMap;
import java.util.Map;

/**
 * Automatically seeds default Administrator account, all 6 Categories, and complete 87 OptiNova Eyewear Products into the database.
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
        seedFullCatalog();
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

    private void seedFullCatalog() {
        log.info("Checking product catalog count in database...");
        if (productRepository.count() >= 80) {
            log.info("Complete 87-product catalog already exists in database. Skipping catalog seed.");
            return;
        }

        log.info("Seeding full 87 OptiNova Eyewear Products and 6 Categories into database...");

        // Ensure 6 Categories exist
        Map<Integer, Category> categories = new HashMap<>();
        categories.put(1, getOrCreateCategory(1, "Prescription Glasses"));
        categories.put(2, getOrCreateCategory(2, "Reading Glass"));
        categories.put(3, getOrCreateCategory(3, "Sunglasses"));
        categories.put(4, getOrCreateCategory(4, "Digital Glass"));
        categories.put(5, getOrCreateCategory(5, "Luxury Glasses"));
        categories.put(6, getOrCreateCategory(6, "Sports Glasses"));

        // Seed 87 Products with exact prices, stock counts, descriptions & ImageKit URLs
        addProduct(1, "Clubmaster", "Classic Clubmaster prescription glasses featuring a timeless browline frame, lightweight construction, comfortable nose pads, and durable materials.", new BigDecimal("9000.00"), 23, categories.get(1), "https://ik.imagekit.io/StringStackmeghana/glasses/1%20%20%20Clubmaster.jpg?updatedAt=1785242393811");
        addProduct(2, "Round Metal", "Elegant Round Metal prescription glasses with a slim stainless steel frame, lightweight design, and premium comfort.", new BigDecimal("8500.00"), 30, categories.get(1), "https://ik.imagekit.io/StringStackmeghana/glasses/2%20%20%20Round%20Metal.jpg");
        addProduct(3, "Aviator Optical", "Modern Aviator Optical prescription glasses with a lightweight metal frame, adjustable nose pads, and stylish oversized design.", new BigDecimal("9800.00"), 21, categories.get(1), "https://ik.imagekit.io/StringStackmeghana/glasses/3%20%20%20%20%20Aviator%20Optical.jpg");
        addProduct(4, "Wayfarer Optical", "Iconic Wayfarer Optical prescription glasses with durable acetate construction, comfortable fit, and versatile style.", new BigDecimal("9200.00"), 26, categories.get(1), "https://ik.imagekit.io/StringStackmeghana/glasses/4%20%20%20%20Wayfarer%20Optical.jpg");
        addProduct(5, "Hexagonal Optical", "Contemporary Hexagonal Optical prescription glasses featuring geometric styling, lightweight metal frame, and comfortable all-day wear.", new BigDecimal("9400.00"), 21, categories.get(1), "https://ik.imagekit.io/StringStackmeghana/glasses/5%20%20%20%20%20%20%20Hexagonal%20Optical.jpg");
        addProduct(6, "Rectangle Classic", "Rectangle Classic prescription glasses designed with a sleek rectangular frame, durable hinges, and lightweight construction.", new BigDecimal("8700.00"), 35, categories.get(1), "https://ik.imagekit.io/StringStackmeghana/glasses/6%20%20%20%20Rectangle%20Classic.jpg");
        addProduct(7, "Square Frame", "Stylish Square Frame prescription glasses offering a bold contemporary look, premium acetate material, and excellent durability.", new BigDecimal("9100.00"), 26, categories.get(1), "https://ik.imagekit.io/StringStackmeghana/glasses/7%20%20%20%20Square%20Frame.jpg");
        addProduct(8, "Cat-Eye Classic", "Elegant Cat-Eye Classic prescription glasses with graceful curves, lightweight construction, and fashionable styling.", new BigDecimal("9500.00"), 20, categories.get(1), "https://ik.imagekit.io/StringStackmeghana/glasses/8%20%20%20%20%20Cat-Eye%20Classic.jpg");
        addProduct(9, "Browline Frame", "Premium Browline Frame prescription glasses combining classic styling with modern comfort and sophisticated appearance.", new BigDecimal("9300.00"), 27, categories.get(1), "https://ik.imagekit.io/StringStackmeghana/glasses/9%20%20%20%20%20%20Browline%20Frame.jpg");
        addProduct(10, "Rimless Air", "Ultra-light Rimless Air prescription glasses providing a minimalist design, crystal-clear vision, and exceptional comfort.", new BigDecimal("9900.00"), 23, categories.get(1), "https://ik.imagekit.io/StringStackmeghana/glasses/10%20%20%20%20Rimless%20Air.jpg");
        addProduct(11, "Half-Rim Executive", "Professional Half-Rim Executive prescription glasses featuring a lightweight metal frame, refined appearance, and superior comfort.", new BigDecimal("9600.00"), 20, categories.get(1), "https://ik.imagekit.io/StringStackmeghana/glasses/11%20%20%20%20Half-Rim%20Executive.jpg");
        addProduct(12, "Geometric Frame", "Fashion-forward Geometric Frame prescription glasses with unique angular styling, premium materials, and lightweight comfort.", new BigDecimal("9700.00"), 24, categories.get(1), "https://ik.imagekit.io/StringStackmeghana/glasses/12%20%20%20%20Geometric%20Frame.jpg");
        addProduct(13, "Pilot Optical", "Classic Pilot Optical prescription glasses inspired by timeless aviator styling, featuring durable metal construction.", new BigDecimal("9800.00"), 24, categories.get(1), "https://ik.imagekit.io/StringStackmeghana/glasses/13%20%20%20%20%20Pilot%20Optical.jpg");
        addProduct(14, "Oval Classic", "Refined Oval Classic prescription glasses with soft contours, lightweight frame, and elegant styling.", new BigDecimal("8800.00"), 30, categories.get(1), "https://ik.imagekit.io/StringStackmeghana/glasses/14%20%20%20%20%20Oval%20Classic.jpg");
        addProduct(15, "Retro Round", "Vintage-inspired Retro Round prescription glasses featuring premium metal construction and classic circular design.", new BigDecimal("9300.00"), 22, categories.get(1), "https://ik.imagekit.io/StringStackmeghana/glasses/15%20%20%20%20%20%20Retro%20Round.jpg");
        addProduct(16, "Transparent Crystal", "Modern Transparent Crystal prescription glasses with crystal-clear acetate frames, lightweight feel, and stylish aesthetics.", new BigDecimal("9100.00"), 29, categories.get(1), "https://ik.imagekit.io/StringStackmeghana/glasses/16%20%20%20%20%20%20Transparent%20Crystal.jpg");
        addProduct(17, "Thin Titanium", "Premium Thin Titanium prescription glasses crafted with ultra-light titanium for maximum durability and long-lasting comfort.", new BigDecimal("12000.00"), 20, categories.get(1), "https://ik.imagekit.io/StringStackmeghana/glasses/17%20%20%20%20Thin%20Titanium.jpg");
        addProduct(18, "Acetate Square", "Contemporary Acetate Square prescription glasses featuring premium acetate material, bold styling, and ergonomic design.", new BigDecimal("9400.00"), 27, categories.get(1), "https://ik.imagekit.io/StringStackmeghana/glasses/18%20%20%20%20Acetate%20Square.jpg");
        addProduct(19, "Flexi Comfort", "Flexible Flexi Comfort prescription glasses with memory-flex frame technology, lightweight construction, and superior comfort.", new BigDecimal("9600.00"), 32, categories.get(1), "https://ik.imagekit.io/StringStackmeghana/glasses/19%20%20%20%20%20Flexi%20Comfort.jpg");
        addProduct(20, "Vintage Metal", "Sophisticated Vintage Metal prescription glasses combining timeless craftsmanship, durable metal construction, and elegant styling.", new BigDecimal("9900.00"), 24, categories.get(1), "https://ik.imagekit.io/StringStackmeghana/glasses/20%20%20%20%20%20%20%20Vintage%20Metal.jpg");

        // Reading Glasses (Category 2)
        addProduct(21, "Zenni Optical", "Affordable Zenni Optical reading glasses featuring lightweight frames, crystal-clear lenses, and comfortable construction.", new BigDecimal("2500.00"), 34, categories.get(2), "https://ik.imagekit.io/StringStackAkash/reading%20Glass/Zenni%20Optical.webp");
        addProduct(22, "Warby Parker", "Premium Warby Parker reading glasses with stylish acetate frames, scratch-resistant lenses, and exceptional comfort.", new BigDecimal("4200.00"), 30, categories.get(2), "https://ik.imagekit.io/StringStackAkash/reading%20Glass/Warby%20Parker.webp");
        addProduct(23, "TruVision Readers", "Comfortable TruVision Readers designed with lightweight materials, durable hinges, and high-quality magnification.", new BigDecimal("2800.00"), 28, categories.get(2), "https://ik.imagekit.io/StringStackAkash/reading%20Glass/TruVision%20Readers.webp");
        addProduct(24, "ThinOptics", "Ultra-slim ThinOptics reading glasses featuring a compact portable design, lightweight construction, and clear vision.", new BigDecimal("3500.00"), 26, categories.get(2), "https://ik.imagekit.io/StringStackAkash/reading%20Glass/ThinOptics.webp");
        addProduct(25, "Readers", "Classic Readers with ergonomic frames, premium optical lenses, and lightweight comfort for reading and studying.", new BigDecimal("2200.00"), 40, categories.get(2), "https://ik.imagekit.io/StringStackAkash/reading%20Glass/Readers.webp");
        addProduct(26, "Ray-Ban Readers", "Stylish Ray-Ban Readers combining iconic design, premium materials, and crystal-clear reading lenses.", new BigDecimal("6500.00"), 22, categories.get(2), "https://ik.imagekit.io/StringStackAkash/reading%20Glass/Ray-Ban%20Readers.webp");
        addProduct(27, "Polaroid Eyewear", "Polaroid Eyewear reading glasses featuring lightweight frames, durable construction, and comfortable fit.", new BigDecimal("3800.00"), 25, categories.get(2), "https://ik.imagekit.io/StringStackAkash/reading%20Glass/Polaroid%20Eyewear.webp");
        addProduct(28, "Persol", "Luxury Persol reading glasses crafted with premium acetate, elegant Italian styling, and precision optical lenses.", new BigDecimal("7800.00"), 18, categories.get(2), "https://ik.imagekit.io/StringStackAkash/reading%20Glass/Persol.webp?updatedAt=1785234024538");
        addProduct(29, "Peepers", "Fashionable Peepers reading glasses offering vibrant frame designs, lightweight construction, and premium lenses.", new BigDecimal("3200.00"), 27, categories.get(2), "https://ik.imagekit.io/StringStackAkash/reading%20Glass/Peepers.webp?updatedAt=1785234006074");
        addProduct(30, "Oakley", "Performance-inspired Oakley reading glasses featuring durable construction, lightweight frames, and premium optical clarity.", new BigDecimal("7200.00"), 20, categories.get(2), "https://ik.imagekit.io/StringStackAkash/reading%20Glass/Oakley.webp?updatedAt=1785233991078");
        addProduct(31, "Nooz Optics", "Minimalist Nooz Optics reading glasses designed with ultra-light materials, compact portability, and exceptional comfort.", new BigDecimal("3400.00"), 24, categories.get(2), "https://ik.imagekit.io/StringStackAkash/reading%20Glass/Nooz%20Optics.webp?updatedAt=1785233975048");
        addProduct(32, "MODO", "Premium MODO reading glasses crafted from lightweight materials with sleek styling, durable hinges, and comfortable all-day wear.", new BigDecimal("6900.00"), 21, categories.get(2), "https://ik.imagekit.io/StringStackAkash/reading%20Glass/MODO.webp?updatedAt=1785233959393");
        addProduct(33, "Gunnar (Computer Readers)", "Advanced Gunnar Computer Readers featuring blue light filtering technology, anti-glare lenses, and ergonomic frames.", new BigDecimal("5600.00"), 23, categories.get(2), "https://ik.imagekit.io/StringStackAkash/reading%20Glass/Gunnar%20(Computer%20Readers).webp?updatedAt=1785233940161");
        addProduct(34, "GlassesUSA", "Modern GlassesUSA reading glasses with durable frames, precision optical lenses, and lightweight comfort.", new BigDecimal("3600.00"), 29, categories.get(2), "https://ik.imagekit.io/StringStackAkash/reading%20Glass/GlassesUSA.webp?updatedAt=1785233922718");
        addProduct(35, "Gamma Ray Optics", "Gamma Ray Optics reading glasses offering crystal-clear magnification, lightweight construction, and stylish everyday comfort.", new BigDecimal("2700.00"), 31, categories.get(2), "https://ik.imagekit.io/StringStackAkash/reading%20Glass/Gamma%20Ray%20Optics.webp?updatedAt=1785233908893");
        addProduct(36, "Foster Grant", "Trusted Foster Grant reading glasses featuring durable frames, comfortable nose pads, and premium optical lenses.", new BigDecimal("3000.00"), 32, categories.get(2), "https://ik.imagekit.io/StringStackAkash/reading%20Glass/Foster%20Grant.webp?updatedAt=1785233895779");
        addProduct(37, "EyeBuyDirect", "Contemporary EyeBuyDirect reading glasses with modern styling, premium-quality lenses, and lightweight frames.", new BigDecimal("3900.00"), 26, categories.get(2), "https://ik.imagekit.io/StringStackAkash/reading%20Glass/EyeBuyDirect.webp?updatedAt=1785233879411");
        addProduct(38, "Eyebobs", "Designer Eyebobs reading glasses featuring bold frame designs, premium craftsmanship, and clear magnified vision.", new BigDecimal("5800.00"), 20, categories.get(2), "https://ik.imagekit.io/StringStackAkash/reading%20Glass/Eyebobs.webp?updatedAt=1785233858603");
        addProduct(39, "Clic Eyewear", "Innovative Clic Eyewear reading glasses with a magnetic front connection, lightweight construction, and convenient accessibility.", new BigDecimal("4700.00"), 22, categories.get(2), "https://ik.imagekit.io/StringStackAkash/reading%20Glass/Clic%20Eyewear.webp?updatedAt=1785233844916");
        addProduct(40, "Carrera", "Premium Carrera reading glasses combining sporty styling, durable construction, and crystal-clear reading lenses.", new BigDecimal("6100.00"), 24, categories.get(2), "https://ik.imagekit.io/StringStackAkash/reading%20Glass/Carrera.webp?updatedAt=1785233252971");

        // Sunglasses (Category 3)
        addProduct(41, "ARZONAI", "Stylish ARZONAI sunglasses featuring UV400 protection, lightweight frame, polarized lenses, and modern design.", new BigDecimal("3200.00"), 30, categories.get(3), "https://ik.imagekit.io/StringStackgangadhar/Sunglasses/ARZONAI.jpg?updatedAt=1785239688042");
        addProduct(42, "SYGA", "Premium SYGA sunglasses with polarized UV400 lenses, durable frame construction, and all-day comfort.", new BigDecimal("3500.00"), 28, categories.get(3), "https://ik.imagekit.io/StringStackgangadhar/Sunglasses/SYGA.jpg?updatedAt=1785239597020");
        addProduct(43, "ROZIOR", "Elegant ROZIOR sunglasses designed with scratch-resistant polarized lenses, lightweight frame, and superior UV protection.", new BigDecimal("3800.00"), 25, categories.get(3), "https://ik.imagekit.io/StringStackgangadhar/Sunglasses/ROZIOR.jpg?updatedAt=17852395283854");
        addProduct(44, "PIRASO", "Classic PIRASO sunglasses featuring stylish frames, polarized UV400 lenses, and lightweight comfort.", new BigDecimal("2900.00"), 35, categories.get(3), "https://ik.imagekit.io/StringStackgangadhar/Sunglasses/PIRASO.jpg?updatedAt=1785239495579");
        addProduct(45, "PETER JONES", "Premium PETER JONES sunglasses crafted with durable materials, polarized lenses, and sophisticated design.", new BigDecimal("4200.00"), 24, categories.get(3), "https://ik.imagekit.io/StringStackgangadhar/Sunglasses/PETER%20JONES.jpg?updatedAt=1785239471412");
        addProduct(46, "OKNO", "Modern OKNO sunglasses featuring lightweight construction, UV400 protection, and crystal-clear polarized lenses.", new BigDecimal("3600.00"), 27, categories.get(3), "https://ik.imagekit.io/StringStackgangadhar/Sunglasses/OKNO.jpg?updatedAt=1785239449641");
        addProduct(47, "Nuvew", "Fashionable Nuvew sunglasses with premium polarized lenses, stylish lightweight frames, and superior UV protection.", new BigDecimal("4100.00"), 22, categories.get(3), "https://ik.imagekit.io/StringStackgangadhar/Sunglasses/Nuvew.webp?updatedAt=17852394203808");
        addProduct(48, "NHCDFA", "Trendy NHCDFA sunglasses offering polarized UV400 lenses, ergonomic frame design, and excellent durability.", new BigDecimal("3300.00"), 26, categories.get(3), "https://ik.imagekit.io/StringStackgangadhar/Sunglasses/NHCDFA.jpg?updatedAt=17852393982709");
        addProduct(49, "LUIS MERCOS", "Luxury LUIS MERCOS sunglasses combining elegant styling, premium polarized lenses, and lightweight comfort.", new BigDecimal("5200.00"), 20, categories.get(3), "https://ik.imagekit.io/StringStackgangadhar/Sunglasses/LUIS%20MERCOS.jpg?updatedAt=1785239355801");
        addProduct(50, "KARSAER", "Contemporary KARSAER sunglasses featuring durable metal frames, polarized lenses, and premium UV protection.", new BigDecimal("4700.00"), 23, categories.get(3), "https://ik.imagekit.io/StringStackgangadhar/Sunglasses/KARSAER.jpg?updatedAt=1785239285685");
        addProduct(51, "dervin", "Versatile dervin sunglasses with lightweight frames, scratch-resistant polarized lenses, and all-day comfort.", new BigDecimal("3400.00"), 29, categories.get(3), "https://ik.imagekit.io/StringStackgangadhar/Sunglasses/dervin.jpg?updatedAt=1785235946945");
        addProduct(52, "Zegna", "Luxury Zegna sunglasses crafted with premium materials, designer styling, polarized lenses, and exceptional UV protection.", new BigDecimal("9800.00"), 18, categories.get(3), "https://ik.imagekit.io/StringStackNaveen/Sunglasses/Zegna12.webp");
        addProduct(53, "Gucci", "Designer Gucci sunglasses featuring iconic styling, premium craftsmanship, polarized lenses, and luxurious comfort.", new BigDecimal("12500.00"), 15, categories.get(3), "https://ik.imagekit.io/StringStackNaveen/Sunglasses/Gucci13.webp");
        addProduct(54, "Ray-Ban Aviator", "Iconic Ray-Ban Aviator sunglasses with timeless metal frames, polarized lenses, UV400 protection, and optical clarity.", new BigDecimal("8900.00"), 20, categories.get(3), "https://ik.imagekit.io/StringStackNaveen/Sunglasses/Ray-Ban%20Aviator14.webp");
        addProduct(55, "Bugatti", "Premium Bugatti sunglasses combining European styling, lightweight construction, polarized lenses, and superior comfort.", new BigDecimal("7600.00"), 18, categories.get(3), "https://ik.imagekit.io/StringStackNaveen/Sunglasses/Bugatti15.png");
        addProduct(56, "Michael Kors", "Fashion-forward Michael Kors sunglasses featuring luxury frames, polarized UV400 lenses, and premium comfort.", new BigDecimal("8400.00"), 19, categories.get(3), "https://ik.imagekit.io/StringStackNaveen/Sunglasses/MichaelKors16.webp");
        addProduct(57, "David Jones", "Sophisticated David Jones sunglasses with durable lightweight frames, polarized lenses, and modern styling.", new BigDecimal("5400.00"), 21, categories.get(3), "https://ik.imagekit.io/StringStackNaveen/Sunglasses/DavidJones17.webp");
        addProduct(58, "Armani Exchange", "Premium Armani Exchange sunglasses featuring designer aesthetics, polarized UV400 lenses, and lightweight construction.", new BigDecimal("9100.00"), 17, categories.get(3), "https://ik.imagekit.io/StringStackNaveen/Sunglasses/ArmaniExchange18.webp");

        // Digital Glasses (Category 4)
        addProduct(59, "XReal", "Advanced XReal smart digital glasses featuring augmented reality display, high-definition visuals, lightweight design, and built-in speakers.", new BigDecimal("49999.00"), 15, categories.get(4), "https://ik.imagekit.io/StringStackAkash/Luxury%20Glass/XReal.jpg");
        addProduct(60, "Vuzix", "Premium Vuzix smart glasses with enterprise-grade augmented reality technology, voice controls, and HD display.", new BigDecimal("58999.00"), 12, categories.get(4), "https://ik.imagekit.io/StringStackAkash/Luxury%20Glass/Vuzix.jpg");
        addProduct(61, "Solos", "Innovative Solos smart glasses featuring AI voice assistant, open-ear audio, fitness tracking, and Bluetooth connectivity.", new BigDecimal("34999.00"), 18, categories.get(4), "https://ik.imagekit.io/StringStackAkash/Luxury%20Glass/Solos.jpg");
        addProduct(62, "Snap", "Snap smart glasses with integrated HD camera, hands-free photo and video capture, and Bluetooth connectivity.", new BigDecimal("29999.00"), 20, categories.get(4), "https://ik.imagekit.io/StringStackAkash/Luxury%20Glass/Snap.webp");
        addProduct(63, "Rokid", "Next-generation Rokid AR smart glasses featuring immersive augmented reality display, voice interaction, and HD visuals.", new BigDecimal("46999.00"), 14, categories.get(4), "https://ik.imagekit.io/StringStackAkash/Luxury%20Glass/Rokid.jpg");
        addProduct(64, "Brilliant Labs", "Brilliant Labs AI smart glasses equipped with real-time AI assistance, advanced AR technology, and wireless connectivity.", new BigDecimal("42999.00"), 16, categories.get(4), "https://ik.imagekit.io/StringStackAkash/Luxury%20Glass/Brilliant%20Labs.jpg");
        addProduct(65, "Rayban Smart", "Premium Ray-Ban smart glasses combining iconic fashion with integrated camera, open-ear speakers, and voice assistant.", new BigDecimal("38999.00"), 18, categories.get(4), "https://ik.imagekit.io/StringStackAkash/Luxury%20Glass/Rayban.jpg");
        addProduct(66, "Lenovo Smart", "Lenovo smart glasses offering immersive virtual display technology, Full HD visuals, USB-C connectivity, and ergonomic design.", new BigDecimal("44999.00"), 13, categories.get(4), "https://ik.imagekit.io/StringStackAkash/Luxury%20Glass/Lenovo.jpg");
        addProduct(67, "Google Glass", "Google smart glasses featuring AI-powered assistance, augmented reality capabilities, voice commands, and Android integration.", new BigDecimal("54999.00"), 10, categories.get(4), "https://ik.imagekit.io/StringStackAkash/Luxury%20Glass/Google.jpg");
        addProduct(68, "Even Realities", "Even Realities smart glasses with AI-powered display, real-time translation, navigation assistance, and lightweight comfort.", new BigDecimal("51999.00"), 11, categories.get(4), "https://ik.imagekit.io/StringStackAkash/Luxury%20Glass/Even%20Realities.jpg");

        // Luxury Glasses (Category 5)
        addProduct(69, "Matsuda", "Luxury Matsuda eyewear handcrafted in Japan with premium titanium construction, precision craftsmanship, and elegant styling.", new BigDecimal("45000.00"), 10, categories.get(5), "https://ik.imagekit.io/StringStackAkash/LX/Matsuda.jpg");
        addProduct(70, "Maybach Eyewear", "Exclusive Maybach Eyewear featuring handcrafted premium materials, gold-plated detailing, and world-class optical craftsmanship.", new BigDecimal("85000.00"), 6, categories.get(5), "https://ik.imagekit.io/StringStackAkash/LX/Maybach%20Eyewear.jpg");
        addProduct(71, "Lindberg", "Minimalist Lindberg luxury eyewear crafted from ultra-light titanium with screwless engineering and unmatched all-day comfort.", new BigDecimal("52000.00"), 9, categories.get(5), "https://ik.imagekit.io/StringStackAkash/LX/Lindberg.jpg");
        addProduct(72, "DITA", "Premium DITA luxury eyewear combining Japanese craftsmanship, bold contemporary styling, and lightweight titanium frames.", new BigDecimal("58000.00"), 8, categories.get(5), "https://ik.imagekit.io/StringStackAkash/LX/DITA.jpg");
        addProduct(73, "Chrome Heart", "Handcrafted Chrome Heart luxury glasses with sterling silver ornaments, distinctive design, and premium optical lenses.", new BigDecimal("75000.00"), 7, categories.get(5), "https://ik.imagekit.io/StringStackAkash/LX/Chrome%20Heart.jpg");
        addProduct(74, "Cartier", "Prestigious Cartier luxury glasses featuring gold finishes, iconic C-De-Cartier hinges, and timeless French craftsmanship.", new BigDecimal("92000.00"), 5, categories.get(5), "https://ik.imagekit.io/StringStackAkash/LX/Cartier.jpg");
        addProduct(75, "Blackfin", "Italian Blackfin titanium luxury glasses featuring ultra-lightweight biocompatible titanium and hand-painted finishes.", new BigDecimal("48000.00"), 11, categories.get(5), "https://ik.imagekit.io/StringStackAkash/LX/Blackfin.jpg");
        addProduct(76, "Akoni", "Akoni luxury eyewear engineered with Swiss precision, Japanese titanium, and sophisticated architectural aesthetic.", new BigDecimal("62000.00"), 8, categories.get(5), "https://ik.imagekit.io/StringStackAkash/LX/Akoni.jpg");
        addProduct(77, "Barton Perreira", "Handmade Barton Perreira luxury glasses with custom zyl acetate, refined contours, and exceptional optical clarity.", new BigDecimal("51000.00"), 10, categories.get(5), "https://ik.imagekit.io/StringStackAkash/LX/Barton%20Perreira.jpg");
        addProduct(78, "JACOB", "Ultra-exclusive JACOB luxury glasses crafted with precious metals, diamond-cut detailing, and master Italian craftsmanship.", new BigDecimal("110000.00"), 4, categories.get(5), "https://ik.imagekit.io/StringStackAkash/LX/JACOB.jpg");

        // Sports Glasses (Category 6)
        addProduct(79, "Carrera Sports", "High-performance Carrera Sports glasses featuring aerodynamic wrap-around frames, shatterproof lenses, and non-slip rubber grips.", new BigDecimal("14500.00"), 18, categories.get(6), "https://ik.imagekit.io/StringStackNaveen/Sports/Carrera1.avif");
        addProduct(80, "Oakley Sports", "Oakley performance sports glasses featuring Prizm lens technology, impact-resistant O-Matter frames, and max ventilation.", new BigDecimal("18900.00"), 22, categories.get(6), "https://ik.imagekit.io/StringStackNaveen/Sports/Oakley2.avif");
        addProduct(81, "Reebok Sports", "Durable Reebok Sports glasses with flexible TR90 frames, polarized UV400 lenses, and sweat-resistant nose pads.", new BigDecimal("8900.00"), 30, categories.get(6), "https://ik.imagekit.io/StringStackNaveen/Sports/Reebok3.webp");
        addProduct(82, "Nike Sports", "Nike athletic performance sports glasses engineered with Max Optics clarity, ventilated nose bridge, and lightweight fit.", new BigDecimal("16500.00"), 25, categories.get(6), "https://ik.imagekit.io/StringStackNaveen/Sports/Nike4.avif");
        addProduct(83, "Revo Sports", "Revo polarized sports glasses featuring NASA-derived lens technology, water-repellent coating, and impact resistance.", new BigDecimal("19500.00"), 15, categories.get(6), "https://ik.imagekit.io/StringStackNaveen/Sports/Revo5.avif");
        addProduct(84, "Rudy Project", "Rudy Project professional cycling sports glasses with adjustable temple tips, photochromic lenses, and ultra-light chassis.", new BigDecimal("22000.00"), 14, categories.get(6), "https://ik.imagekit.io/StringStackNaveen/Sports/Rudyproject6.avif");
        addProduct(85, "SmartBuy Collection", "SmartBuy Collection sports glasses offering lightweight TR90 frames, UV400 protection, and affordable athletic style.", new BigDecimal("6500.00"), 35, categories.get(6), "https://ik.imagekit.io/StringStackNaveen/Sports/SmartBuycollection7.webp");
        addProduct(86, "Tommy Hilfiger Sports", "Tommy Hilfiger sports sunglasses combining athletic styling, polarized UV protection, and rubberized ear grips.", new BigDecimal("12800.00"), 20, categories.get(6), "https://ik.imagekit.io/StringStackNaveen/Sports/TommyHilfiger8.webp");
        addProduct(87, "Maui Jim Sports", "Maui Jim premium sports sunglasses featuring PolarizedPlus2 lens technology, vibrant color enhancement, and glare elimination.", new BigDecimal("24500.00"), 16, categories.get(6), "https://ik.imagekit.io/StringStackNaveen/Sports/Maui%20Jim9.webp");

        log.info("Successfully seeded complete 87 OptiNova Eyewear Products into database!");
    }

    private Category getOrCreateCategory(Integer id, String name) {
        return categoryRepository.findByCategoryName(name)
                .orElseGet(() -> categoryRepository.save(
                        Category.builder()
                                .categoryId(id)
                                .categoryName(name)
                                .build()
                ));
    }

    private void addProduct(Integer id, String name, String desc, BigDecimal price, int stock, Category category, String imageUrl) {
        if (productRepository.existsById(id) || productRepository.findByName(name).isPresent()) {
            return;
        }

        Product product = Product.builder()
                .productId(id)
                .name(name)
                .description(desc)
                .price(price)
                .costPrice(price.multiply(new BigDecimal("0.60")).setScale(2, java.math.RoundingMode.HALF_UP))
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
