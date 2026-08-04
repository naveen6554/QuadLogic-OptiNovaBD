package com.optinova.repository;

import com.optinova.entity.JwtToken;
import com.optinova.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository interface for JwtToken entity management.
 * Provides query methods for token retrieval, validation, and user session deletion.
 */
@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, Integer> {

    Optional<JwtToken> findByToken(String token);

    @Query("SELECT t FROM JwtToken t WHERE t.user.userId = :userId")
    List<JwtToken> findAllValidTokensByUser(@Param("userId") Integer userId);

    Optional<JwtToken> findByUser(User user);

    Optional<JwtToken> findByUserUserId(Integer userId);

    @Modifying
    @Transactional
    void deleteByUser(User user);

    @Modifying
    @Transactional
    void deleteByUserUserId(Integer userId);
}
