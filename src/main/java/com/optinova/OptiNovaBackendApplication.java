package com.optinova;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Entry Point for the OptiNova Backend Application.
 * <p>
 * OptiNova is an enterprise optical e-commerce backend built with Spring Boot 3,
 * Spring Security 6, JWT Authentication, and Spring Data JPA.
 * </p>
 *
 * @author OptiNova Engineering Team
 * @version 1.0.0
 */
@SpringBootApplication
public class OptiNovaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(OptiNovaBackendApplication.class, args);
    }
}
