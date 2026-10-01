package com.stockpilot.product.repository;

import com.stockpilot.product.model.Product;
import com.stockpilot.product.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductJpaRepository
        extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    boolean existsBySku(String sku);

    boolean existsByCategoryIdAndStatus(Long categoryId, ProductStatus status);

    boolean existsBySupplierIdAndStatus(Long supplierId, ProductStatus status);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findByStockStatus(com.stockpilot.product.model.StockStatus stockStatus, Pageable pageable);
}