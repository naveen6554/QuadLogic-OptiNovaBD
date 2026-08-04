package com.optinova.repository;

import com.optinova.entity.User;
import com.optinova.entity.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test suite for UserRepository JPA operations using in-memory H2 database under test profile.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should Save User and Retrieve By Email")
    void testSaveAndFindByEmail() {
        User user = User.builder()
                .username("optitester")
                .email("test.user@optinova.com")
                .password("encoded_secret_password")
                .role(Role.CUSTOMER)
                .build();

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getUserId());
        Optional<User> retrieved = userRepository.findByEmail("test.user@optinova.com");
        assertTrue(retrieved.isPresent());
        assertEquals("optitester", retrieved.get().getUsername());
    }
}
