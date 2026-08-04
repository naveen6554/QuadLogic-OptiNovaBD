package com.optinova.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA Entity mapping to the 'categories' table in the database.
 * Columns: category_id, category_name
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "category_name", nullable = false, unique = true, length = 255)
    private String categoryName;

    public Integer getId() {
        return categoryId;
    }

    public void setId(Integer id) {
        this.categoryId = id;
    }

    public String getName() {
        return categoryName;
    }

    public void setName(String name) {
        this.categoryName = name;
    }
}
