package com.optinova.repository;

import com.optinova.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository interface for CartItem entity.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    List<CartItem> findByUserUserId(Integer userId);

    Optional<CartItem> findByUserUserIdAndProductProductId(Integer userId, Integer productId);

    void deleteByUserUserId(Integer userId);
}
