package com.optinova.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * JPA Entity mapping to the 'order_items' table in the database.
 * Columns: id, order_id, product_id, quantity, price_per_unit, total_price
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price_per_unit", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerUnit;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    public BigDecimal getPrice() {
        return pricePerUnit;
    }

    public void setPrice(BigDecimal price) {
        this.pricePerUnit = price;
    }

    public BigDecimal getSubtotal() {
        return totalPrice;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.totalPrice = subtotal;
    }
}
