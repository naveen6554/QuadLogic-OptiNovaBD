package com.optinova.config;

import com.optinova.entity.User;
import com.optinova.entity.enums.Role;
import com.optinova.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Automatically seeds default Administrator account on application startup if missing.
 * Preserves user's custom MySQL database tables, categories, and products without overriding.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedAdminUser();
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
}
