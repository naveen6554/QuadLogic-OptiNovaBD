package com.optinova.repository;

import com.optinova.entity.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository interface for JwtToken entity management.
 */
@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {

    Optional<JwtToken> findByToken(String token);

    @Query("SELECT t FROM JwtToken t WHERE t.user.id = :userId AND (t.expired = false OR t.revoked = false)")
    List<JwtToken> findAllValidTokensByUser(@Param("userId") Long userId);
}
