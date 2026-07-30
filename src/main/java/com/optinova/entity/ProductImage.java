package com.optinova.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA Entity mapping to the 'productimages' table in the 'e-commerce' database.
 * Stores optical product gallery images and primary thumbnail flag.
 */
@Entity
@Table(name = "productimages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private boolean isPrimary = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
