package com.optinova.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA Entity mapping to the 'productimages' table in the database.
 * Columns: image_id, product_id, image_url
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
    @Column(name = "image_id")
    private Integer imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    public Integer getId() {
        return imageId;
    }

    public void setId(Integer id) {
        this.imageId = id;
    }
}
