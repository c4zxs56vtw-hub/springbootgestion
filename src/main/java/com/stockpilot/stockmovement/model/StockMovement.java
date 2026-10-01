package com.stockpilot.stockmovement.model;


import com.stockpilot.product.model.Product;
import com.stockpilot.supplier.model.Supplier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name ="stock_movement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;

    @Column(nullable = false)
    private Integer quantityBefore;

    @Column(nullable = false)
    private Integer quantityDelta;

    @Column(nullable = false)
    private Integer quantityAfter;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    private String reference;

    private String note;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}




