package com.optinova;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration Context Test for OptiNova Backend.
 * Verifies that the Spring Application Context loads successfully under the test profile.
 */
@SpringBootTest
@ActiveProfiles("test")
class OptiNovaBackendApplicationTests {

    @Test
    @DisplayName("Verify Spring Application Context Initialization")
    void contextLoads() {
        assertTrue(true, "Application context initialized successfully");
    }
}
