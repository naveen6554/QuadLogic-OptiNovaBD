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
 * Automatically seeds the default Administrator account on application startup if missing.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        userRepository.findByUsername("optiadmin").ifPresentOrElse(
                existingAdmin -> {
                    // Update password and role to guarantee optiadmin access
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
