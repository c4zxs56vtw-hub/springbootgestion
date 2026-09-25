package com.stockpilot.product.repository;

import com.stockpilot.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository
        extends JpaRepository<Product, Long> {

    boolean existsBySku(String sku);
}